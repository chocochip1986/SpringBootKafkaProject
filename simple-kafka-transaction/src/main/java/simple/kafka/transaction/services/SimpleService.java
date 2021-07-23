package simple.kafka.transaction.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simple.kafka.transaction.dto.DtoOne;
import simple.kafka.transaction.dto.DtoTwo;
import simple.kafka.transaction.jpa.AnimalEntity;
import simple.kafka.transaction.jpa.AnimalJpaRepo;
import simple.kafka.transaction.producers.KafkaByteProducer;
import simple.kafka.transaction.producers.TxKafkaByteProducer;

@Service
public class SimpleService {
    @Autowired private KafkaByteProducer kafkaByteProducer;
    @Autowired private TxKafkaByteProducer txKafkaByteProducer;
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

    public void consumeFromTopicFive(DtoOne dto) {
        System.out.println("Consuming DtoOne");
        this.txKafkaByteProducer.sendTxMessage("topic.six", DtoTwo.builder().uuid(dto.getUuid()).build());
        throw new RuntimeException("HA!");
    }

    public void consumeFromTopicSix(DtoTwo dto) {
        System.out.println("Consuming DtoTwo");
    }

    private void waiting() {
        try {
            Thread.sleep(30001);
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted la", e);
        }
    }
}
