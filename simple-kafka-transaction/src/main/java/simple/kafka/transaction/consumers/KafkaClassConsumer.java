package simple.kafka.transaction.consumers;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import simple.kafka.transaction.jpa.AnimalEntity;


@Service
public class KafkaClassConsumer {
    @KafkaListener(topics = "topic.three", containerFactory = "kafkaByteWithConverterListenerContainerFactory")
    public void consume(AnimalEntity record) {
        System.out.println("Message consumed: "+record.getName());
    }
}
