package simple.kafka.transaction.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simple.kafka.transaction.dto.DtoFive;
import simple.kafka.transaction.dto.DtoFour;
import simple.kafka.transaction.dto.DtoOne;
import simple.kafka.transaction.dto.DtoSix;
import simple.kafka.transaction.dto.DtoThree;
import simple.kafka.transaction.dto.DtoTwo;
import simple.kafka.transaction.services.SimpleService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class KafkaByteConsumer {
    @Autowired private SimpleService simpleService;
    @Autowired private ObjectMapper objectMapper;
    @KafkaListener(topics = "topic.one", containerFactory = "txKafkaByteListenerContainerFactory")
    public void consume1(ConsumerRecord<String, byte[]> record) {
        simpleService.consume1(convert(record.value(), DtoOne.class));
    }

    @KafkaListener(topics = "topic.two", containerFactory = "txKafkaByteListenerContainerFactory")
    public void consume2(ConsumerRecord<String, byte[]> record) {
        simpleService.consume2(convert(record.value(), DtoTwo.class));
    }

    @KafkaListener(topics = "topic.three", containerFactory = "txKafkaByteListenerContainerFactory")
    public void consume3(ConsumerRecord<String, byte[]> record) {
        simpleService.consume3(convert(record.value(), DtoThree.class));
    }

    @KafkaListener(topics = "topic.four", containerFactory = "txKafkaByteListenerContainerFactory")
    public void consume4(ConsumerRecord<String, byte[]> record) {
        simpleService.consume4(convert(record.value(), DtoFour.class));
    }

    @KafkaListener(topics = "topic.five", containerFactory = "txKafkaByteListenerContainerFactory")
    public void consume5(ConsumerRecord<String, byte[]> record) {
        simpleService.consume5(convert(record.value(), DtoFive.class));
    }

    @KafkaListener(topics = "topic.six", containerFactory = "txKafkaByteListenerContainerFactory")
    public void consume6(ConsumerRecord<String, byte[]> record) {
        simpleService.consume6(convert(record.value(), DtoSix.class));
    }

    private <T> T convert(byte[] payload, Class<T> klazz) {
        try {
            return this.objectMapper.readValue(payload, klazz);
        } catch (IOException e) {
            throw new RuntimeException("Unable to convert from byte[] to "+klazz.getName(), e);
        }
    }
}
