package rebalancing.consumers;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "topic.one", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, String> record) {
        if ( record.value().equals("5") ) {
            throw new RuntimeException("THROW AH!");
        }
        System.out.println("Message value: "+record.value());
    }
}
