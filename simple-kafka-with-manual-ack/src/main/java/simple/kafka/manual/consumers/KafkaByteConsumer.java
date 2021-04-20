package simple.kafka.manual.consumers;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class KafkaByteConsumer {
    @KafkaListener(topics = "topic.two", containerFactory = "kafkaByteListenerContainerFactory")
    public void consume(ConsumerRecord<String, byte[]> record, Acknowledgment acknowledgment) {
        System.out.println("Message consumed: "+new String(record.value(), StandardCharsets.UTF_8));
        acknowledgment.acknowledge();
    }
}
