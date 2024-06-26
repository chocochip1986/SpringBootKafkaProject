package simple.kafka.transaction.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simple.kafka.transaction.dto.DtoFive;
import simple.kafka.transaction.dto.DtoFour;
import simple.kafka.transaction.dto.DtoOne;
import simple.kafka.transaction.dto.DtoSix;
import simple.kafka.transaction.dto.DtoThree;
import simple.kafka.transaction.dto.DtoTwo;
import simple.kafka.transaction.jpa.AnimalEntity;
import simple.kafka.transaction.jpa.AnimalJpaRepo;
import simple.kafka.transaction.producers.KafkaByteProducer;
import simple.kafka.transaction.producers.KafkaProducer;
import simple.kafka.transaction.producers.TxKafkaByteProducer;

@Service
public class SimpleService {
    @Autowired private KafkaByteProducer kafkaByteProducer;
    @Autowired private TxKafkaByteProducer txKafkaByteProducer;
    @Autowired private KafkaProducer kafkaProducer;
    @Autowired private AnimalJpaRepo animalJpaRepo;

    @Transactional
    public void consume1(DtoOne dto) {
        System.out.println("DtoOne -> " +dto.getUuid());
        animalJpaRepo.save(AnimalEntity.builder().name("DtoOne "+dto.getUuid()).build());
        txKafkaByteProducer.sendMessage("topic.two", DtoTwo.builder().uuid(dto.getUuid()).build());
        throw new RuntimeException("HA!");
    }

    @Transactional
    public void consume2(DtoTwo dto) {
        System.out.println("DtoTwo -> " +dto.getUuid());
        animalJpaRepo.save(AnimalEntity.builder().name("DtoTwo "+dto.getUuid()).build());
    }

    public void consume3(DtoThree dto) {
        System.out.println("DtoThree -> " +dto.getUuid());
    }

    public void consume4(DtoFour dto) {
        System.out.println("DtoFour -> " +dto.getUuid());
    }

    public void consume5(DtoFive dto) {
        System.out.println("DtoFive -> " +dto.getUuid());
    }

    public void consume6(DtoSix dto) {
        System.out.println("DtoSix -> " +dto.getUuid());
    }

//    @Transactional
    public void consumeFromTopicFive(DtoOne dto) {
        System.out.println("Consuming DtoOne");
        this.txKafkaByteProducer.sendMessage("topic.six", DtoTwo.builder().uuid(dto.getUuid()).build());
//        waiting();
//        throw new RuntimeException("HA!");
    }

    @Transactional
    public void consumeFromTopicSix(DtoTwo dto) {
        System.out.println("Consuming DtoTwo");
        animalJpaRepo.save(AnimalEntity.builder().name("Lion").build());
//        animalJpaRepo.save(AnimalEntity.builder().name("Lion").build());
        throw new RuntimeException("HA!");
    }

    private void waiting() {
        try {
            Thread.sleep(30001);
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted la", e);
        }
    }
}
