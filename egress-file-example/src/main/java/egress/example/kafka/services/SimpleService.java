package egress.example.kafka.services;

import egress.example.kafka.dtos.DtoThree;
import egress.example.kafka.enums.FileStatus;
import egress.example.kafka.enums.Status;
import egress.example.kafka.jpa.AnimalJpaRepo;
import egress.example.kafka.dtos.DtoOne;
import egress.example.kafka.dtos.DtoTwo;
import egress.example.kafka.entities.Animal;
import egress.example.kafka.jpa.FileJpaRepo;
import egress.example.kafka.pojos.AnimalAggregateResult;
import egress.example.kafka.producers.KafkaByteProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockTimeoutException;
import javax.persistence.PessimisticLockException;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SimpleService {
    @Autowired private KafkaByteProducer kafkaByteProducer;
    @Autowired private AnimalJpaRepo animalJpaRepo;
    @Autowired private FileJpaRepo fileJpaRepo;
    @Autowired private HibernateQueryService hibernateQueryService;

    private String absoluteSubDir = System.getProperty("user.dir") + File.separator + "egress";
    private String absoluteFilePath = absoluteSubDir + File.separator + "egress.txt";

    private final int PAGE_SIZE = 3;
    public void init() {
        File dir = new File(absoluteSubDir);
        if (!dir.exists()) {
            try {
                dir.mkdirs();
            } catch (SecurityException e) {
                e.printStackTrace();
                throw new RuntimeException("Cannot create file directories", e);
            }
        }

        dir = new File(absoluteFilePath);
        if (dir.exists()) {
            dir.delete();
        }

        AnimalAggregateResult animalAggregateResult =
                hibernateQueryService
                        .selectQueryReturnPojo("SELECT new "+AnimalAggregateResult.class.getName()+"(min(A.id) AS minIndex, max(A.id) AS maxIndex, count(A.id) AS totalCount) FROM "+Animal.class.getName()+" AS A WHERE A.status = 'NEW' ORDER BY A.createdAt ASC", AnimalAggregateResult.class);
        egress.example.kafka.entities.File fileEntity = egress.example.kafka.entities.File.builder()
                .uuid(UUID.randomUUID().toString())
                .absFilePath(absoluteFilePath)
                .currentCount(0)
                .totalCount(animalAggregateResult.getTotalCount().intValue())
                .status(FileStatus.NEW)
                .build();
        fileEntity = fileJpaRepo.save(fileEntity);
        send("topic.one", DtoOne.builder().file(fileEntity).result(animalAggregateResult).build());
    }

    public void consumeDtoOne(DtoOne dto) {
        egress.example.kafka.entities.File fileEntity = fileJpaRepo.findById(dto.getFile().getId()).orElse(null);
        if (Objects.nonNull(fileEntity) && fileEntity.getStatus().equals(FileStatus.NEW)) {
            System.out.println("Processing DtoOne...");

            int noOfPages = calculateNoOfPages(fileEntity.getTotalCount());

            fileEntity = dto.getFile();
            fileEntity.setStatus(FileStatus.CONSUMED_DTO_ONE);
            for ( int i = 0 ; i < noOfPages ; i++ ) {
                send("topic.two", DtoTwo.builder().file(fileEntity).index(i).size(PAGE_SIZE).build());
            }
            fileJpaRepo.save(fileEntity);
        } else {
            if (Objects.isNull(fileEntity)) {
                System.out.println("File Entity of id "+dto.getFile().getId()+" is null!");
            } else {
                System.out.println("File Entity of id "+fileEntity.getId()+" of status "+fileEntity.getStatus().toString()+" has already been processed. Skipping...");
            }
        }
    }

    public void consumeDtoTwo(DtoTwo dto) {
        System.out.println("Processing DtoTwo...");
        List<Animal> animals = animalJpaRepo.findByIdWithPageable(PageRequest.of(dto.getIndex(), dto.getSize())).orElse(null);

        if (Objects.isNull(animals) || animals.isEmpty()) {
            System.out.println("Empty or null list of animals retrieved given pageable of ("+dto.getIndex()+", "+dto.getSize()+")");
            throw new RuntimeException("Empty or null list of animals retrieved given pageable of ("+dto.getIndex()+", "+dto.getSize()+")");
        } else {
            System.out.println("Retrieved a list of "+animals.size()+" animals given pageable of ("+dto.getIndex()+", "+dto.getSize()+")");
            send("topic.three", DtoThree.builder().animals(animals).file(dto.getFile()).build());
        }
    }

    @Transactional
    public void consumeDtoThree(DtoThree dto) {
        Long id = dto.getFile().getId();
        File file = new File(dto.getFile().getAbsFilePath());

        egress.example.kafka.entities.File fileEntity;
        try {
            fileEntity = fileJpaRepo.findByIdWithLock(id).orElse(null);
        } catch (LockTimeoutException | PessimisticLockException e) {
            throw new RuntimeException("Unable to lock file entity!", e);
        } catch (Exception e) {
            throw new RuntimeException("Some shit went down horribly man...", e);
        }

        if (Objects.isNull(fileEntity)) {
            throw new RuntimeException("Null file entity is retrieved!");
        }

        if (fileEntity.getStatus().equals(FileStatus.ERROR)) {
            throw new RuntimeException("File entity of id "+id+" already has an error status! No further action is taken!");
        }

        if (fileEntity.getCurrentCount() > fileEntity.getTotalCount()) {
            //TODO update file entity status to error
            throw new RuntimeException(("File entity of id "+id+"'s current count of "+fileEntity.getCurrentCount()+" has already exceeded it's total count of "+fileEntity.getTotalCount()));
        }

        if (fileEntity.getCurrentCount() + dto.getAnimals().size() > fileEntity.getTotalCount()) {
            //TODO update file entity status to error
            throw new RuntimeException(("File entity of id "+id+"'s current count of "+fileEntity.getCurrentCount()+" in addition to dto's count of "+dto.getAnimals().size()+" exceeds it's total count of "+fileEntity.getTotalCount()));
        }

        boolean isLast = false;
        if (fileEntity.getCurrentCount()+dto.getAnimals().size() == fileEntity.getTotalCount()) {
            System.out.println("Processing the last batch of records...");
            fileEntity.setCurrentCount(fileEntity.getTotalCount());
            fileEntity.setStatus(FileStatus.COMPLETED);
            isLast = true;
        } else {
            fileEntity.setCurrentCount(fileEntity.getCurrentCount()+dto.getAnimals().size());
        }


        fileJpaRepo.save(fileEntity);
        animalJpaRepo.saveAll(dto.getAnimals().stream().peek(a -> a.setStatus(Status.COMPLETED)).collect(Collectors.toUnmodifiableList()));
    }

    private void send(String topic, Object dto) {
        kafkaByteProducer.sendMessage(topic, dto);
    }

    private int calculateNoOfPages(int cohort) {
        return cohort < 1 ? 0 : ((cohort - 1) / PAGE_SIZE) + 1;
    }

    private void waiting() {
        try {
            Thread.sleep(30001);
        } catch (InterruptedException e) {
            throw new RuntimeException("Thread waited interrupted",e);
        }
    }
}
