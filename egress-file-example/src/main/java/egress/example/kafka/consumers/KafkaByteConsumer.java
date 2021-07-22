package egress.example.kafka.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import egress.example.kafka.dtos.DtoOne;
import egress.example.kafka.dtos.DtoThree;
import egress.example.kafka.dtos.DtoTwo;
import egress.example.kafka.services.SimpleService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Service
public class KafkaByteConsumer {
    @Autowired private ObjectMapper objectMapper;
    @Autowired private SimpleService simpleService;
    @KafkaListener(topics = "topic.one", containerFactory = "kafkaByteListenerContainerFactory")
    public void consumeDtoOne(ConsumerRecord<String, byte[]> record) {
        DtoOne dto = convert(record.value(), DtoOne.class);
        if(Objects.nonNull(dto)) {
            simpleService.consumeDtoOne(dto);
        }
    }

    @KafkaListener(topics = "topic.two", containerFactory = "kafkaByteListenerContainerFactory")
    public void consumeDtoTwo(ConsumerRecord<String, byte[]> record) {
        DtoTwo dto = convert(record.value(), DtoTwo.class);
        if(Objects.nonNull(dto)) {
            simpleService.consumeDtoTwo(dto);
        }
    }

    @KafkaListener(topics = "topic.three", containerFactory = "kafkaByteListenerContainerFactory")
    public void consumeDtoThree(ConsumerRecord<String, byte[]> record) {
        DtoThree dto = convert(record.value(), DtoThree.class);
        if(Objects.nonNull(dto)) {
            simpleService.consumeDtoThree(dto);
        }
    }

    private <T> T convert(byte[] payload, Class<T> klazz) {
        try {
            return this.objectMapper.readValue(payload, klazz);
        } catch (IOException e) {
            throw new RuntimeException("Cannot convert byte[] to "+klazz.getName(), e);
        }
    }
}
