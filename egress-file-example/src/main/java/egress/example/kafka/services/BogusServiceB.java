package egress.example.kafka.services;

import egress.example.kafka.entities.File;
import egress.example.kafka.jpa.FileJpaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BogusServiceB {
    @Autowired private FileJpaRepo fileJpaRepo;
    public Long createNew() {
        File file = File.builder().currentCount(0).build();

        file = fileJpaRepo.saveAndFlush(file);
        return file.getId();
    }

    @Transactional
    public void update(Long id, int i) {
        File file;
        try {
            file = fileJpaRepo.findByIdWithLock(id).orElse(null);
            System.out.println("Current count = "+file.getCurrentCount());
        } catch (Exception e) {
            throw new RuntimeException("Jialat la!", e);
        }

        file.setCurrentCount(file.getCurrentCount()+i);
        fileJpaRepo.saveAndFlush(file);
        File file1 = fileJpaRepo.findByIdWithLock(id).orElse(null);
        System.out.println("Current count after update = "+file1.getCurrentCount());
    }
}
