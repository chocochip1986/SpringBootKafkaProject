package simple.kafka.manual.consumers;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import spring.kafka.commons.entities.Person;


@Service
public class KafkaClassConsumer {
    @KafkaListener(topics = "topic.three", containerFactory = "kafkaByteWithConverterListenerContainerFactory")
    public void consume(Person record, Acknowledgment acknowledgment) {
        System.out.println("Message consumed: "+record.getName());
        acknowledgment.acknowledge();
    }
}
