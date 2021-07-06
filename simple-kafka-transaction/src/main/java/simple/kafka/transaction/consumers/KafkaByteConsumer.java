package simple.kafka.transaction.consumers;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simple.kafka.transaction.services.SimpleService;

import java.nio.charset.StandardCharsets;

@Service
public class KafkaByteConsumer {
    @Autowired private SimpleService simpleService;
    @KafkaListener(topics = "topic.two", containerFactory = "kafkaByteListenerContainerFactory")
    public void consumeFromTopicTwo(ConsumerRecord<String, byte[]> record) {
        simpleService.consumeAndSend(new String(record.value(), StandardCharsets.UTF_8));
    }

    @KafkaListener(topics = "topic.four", containerFactory = "kafkaByteListenerContainerFactory")
    public void consumeFromTopicFour(ConsumerRecord<String, byte[]> record) {
        simpleService.consume(new String(record.value(), StandardCharsets.UTF_8));
    }
}
