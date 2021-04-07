package simple.batch.kafka.consumers;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaConverterConsumer {
    @KafkaListener(topics = "topic.three", containerFactory = "kafkaListenerWithConverterContainerFactory")
    public void consume(List<Person> records) {
        System.out.println("Record Batch Size: "+records.size());
        for(Person record: records) {
            System.out.println("Message consumed: "+record.getName());
        }
    }
}
