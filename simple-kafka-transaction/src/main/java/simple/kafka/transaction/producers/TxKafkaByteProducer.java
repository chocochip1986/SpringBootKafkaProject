package simple.kafka.transaction.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Service
public class TxKafkaByteProducer {
    @Autowired
    private KafkaTemplate<String, byte[]> txKafkaByteTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendMessage(String topic, Object obj) {
        this.txKafkaByteTemplate.send(topic, convert(obj));
    }

    @Transactional
    public void sendTxMessage(String topic, final Object obj) {
        this.txKafkaByteTemplate.executeInTransaction(t -> {
            t.send(topic, convert(obj));
            return true;
        });
    }

    private byte[] convert(Object obj) {
        try {
            return this.objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to convert "+obj.getClass()+" to byte[]!", e);
        }
    }
}
