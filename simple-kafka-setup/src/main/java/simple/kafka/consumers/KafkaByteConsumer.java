package simple.kafka.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import simple.kafka.entities.Bar;
import simple.kafka.entities.Foo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.StreamSupport;

@Service
public class KafkaByteConsumer {
    @Autowired private ObjectMapper objectMapper;
    @KafkaListener(topics = "topic.two", containerFactory = "kafkaByteListenerContainerFactory")
    public void consume(ConsumerRecord<String, byte[]> record) {
        byte[] payload = new byte[record.value().length-1];
        System.arraycopy(record.value(), 1, payload, 0, record.value().length-1);
        if ( record.value()[0] == 48) {
            Foo foo = convert(payload, Foo.class);
            System.out.println("Converted to "+foo.getClass().getName());
        } else {
            Bar bar = convert(payload, Bar.class);
            System.out.println("Converted to "+bar.getClass().getName());
        }
        System.out.println("Message consumed: "+new String(record.value(), StandardCharsets.UTF_8));
    }

    private <T> T convert(byte[] payload, Class<T> klazz) {
        try {
            return this.objectMapper.readValue(payload, klazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
