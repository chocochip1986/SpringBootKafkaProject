package sasl.scram.kafka.consumers;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "topic.one", containerFactory = "kafkaListenerContainerFactory")
    public void consume(List<ConsumerRecord<String, String>> records) {
        System.out.println("Record Batch Size: "+records.size());
        for(ConsumerRecord<String, String> record: records) {
            System.out.println("Message consumed: "+record.value());
        }
    }
}
