package simple.kafka.transaction.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simple.kafka.transaction.jpa.AnimalEntity;
import simple.kafka.transaction.jpa.AnimalJpaRepo;
import simple.kafka.transaction.producers.KafkaByteProducer;

@Service
public class SimpleService {
    @Autowired private KafkaByteProducer kafkaByteProducer;
    @Autowired private AnimalJpaRepo animalJpaRepo;
    @Transactional("chainedKafkaTransactionManager")
    public void consumeAndSend(String str) {
        System.out.println("Message consumed: "+str);
        animalJpaRepo.save(AnimalEntity.builder().name(str).build());
        kafkaByteProducer.sendMessage("topic.four", str+" hello");
        throw new RuntimeException("WAH LAN EH");
    }

    public void consume(String str) {
        System.out.println("Message consumed: "+str);
        throw new RuntimeException("WAH LAN EH");
    }
}
