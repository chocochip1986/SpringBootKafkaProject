package simple.kafka.transaction.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simple.kafka.transaction.dto.DtoOne;
import simple.kafka.transaction.dto.DtoTwo;
import simple.kafka.transaction.services.SimpleService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class KafkaByteConsumer {
    @Autowired private SimpleService simpleService;
    @Autowired private ObjectMapper objectMapper;
    @KafkaListener(topics = "topic.two", containerFactory = "kafkaByteListenerContainerFactory")
    public void consumeFromTopicTwo(ConsumerRecord<String, byte[]> record) {
        simpleService.consumeAndSend(new String(record.value(), StandardCharsets.UTF_8));
    }

    @KafkaListener(topics = "topic.four", containerFactory = "kafkaByteListenerContainerFactory")
    public void consumeFromTopicFour(ConsumerRecord<String, byte[]> record) {
        simpleService.consume(new String(record.value(), StandardCharsets.UTF_8));
    }

    @KafkaListener(topics = "topic.five", containerFactory = "txKafkaByteListenerContainerFactory")
    public void consumeFromTopicFiveTx(ConsumerRecord<String, byte[]> record) {
        simpleService.consumeFromTopicFive(convert(record.value(), DtoOne.class));
    }

    @KafkaListener(topics = "topic.six", containerFactory = "txKafkaByteListenerContainerFactory")
    public void consumeFromTopicSixTx(ConsumerRecord<String, byte[]> record) {
        simpleService.consumeFromTopicSix(convert(record.value(), DtoTwo.class));
    }

    private <T> T convert(byte[] payload, Class<T> klazz) {
        try {
            return this.objectMapper.readValue(payload, klazz);
        } catch (IOException e) {
            throw new RuntimeException("Unable to convert from byte[] to "+klazz.getName(), e);
        }
    }
}
