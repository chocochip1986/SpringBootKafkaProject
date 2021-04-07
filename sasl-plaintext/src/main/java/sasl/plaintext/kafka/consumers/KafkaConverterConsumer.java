package sasl.plaintext.kafka.consumers;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import spring.kafka.commons.entities.Person;

import java.util.List;

@Service
public class KafkaConverterConsumer {
    @KafkaListener(topics = "topic.three", containerFactory = "kafkaByteWithConverterListenerContainerFactory")
    public void consume(List<Person> records) {
        System.out.println("Record Batch Size: "+records.size());
        for(Person record: records) {
            System.out.println("Message consumed: "+record.getName());
        }
    }
}
