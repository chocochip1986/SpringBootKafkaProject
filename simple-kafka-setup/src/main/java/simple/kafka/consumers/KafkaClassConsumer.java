package simple.kafka.consumers;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import spring.kafka.commons.entities.Person;

import java.nio.charset.StandardCharsets;

@Service
public class KafkaClassConsumer {
    @KafkaListener(topics = "topic.three", containerFactory = "kafkaByteWithConverterListenerContainerFactory")
    public void consume(Person record) {
        System.out.println("Message consumed: "+record.getName());
    }
}
