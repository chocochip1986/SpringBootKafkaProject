package simple.kafka.recovery.consumers;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class KafkaByteConsumer {
    @KafkaListener(topics = "topic.two", containerFactory = "kafkaByteListenerContainerFactory")
    public void consume(List<ConsumerRecord<String, byte[]>> records) {
        for(ConsumerRecord<String, byte[]> record: records) {
            String str = new String(record.value(), StandardCharsets.UTF_8);
            if ( str.equals("5") ) {
                throw new BatchListenerFailedException("ERROR AHHHH", record);
            }
            System.out.println("Message consumed: "+new String(record.value(), StandardCharsets.UTF_8));
        }
    }
}
