package simple.kafka.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import spring.kafka.commons.entities.Person;

import java.nio.charset.StandardCharsets;

@Service
public class KafkaByteProducer {
    @Autowired
    private KafkaTemplate<String, byte[]> kafkaByteTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendMessage(String topic, String message) {
        kafkaByteTemplate.send(topic, message.getBytes(StandardCharsets.UTF_8));
    }

    public void sendMessage(String topic, Object message) throws JsonProcessingException {
        kafkaByteTemplate.send(topic, objectMapper.writeValueAsBytes(message));
    }

    public void sendMessage(String topic, byte[] message) {
        kafkaByteTemplate.send(topic, message);
    }

    public void sendMessage(String topic, Person message) throws JsonProcessingException {
        kafkaByteTemplate.send(topic, objectMapper.writeValueAsBytes(message));
    }

    public void sendMessageWithReply(String topic, final String message) {
        ListenableFuture<SendResult<String, byte[]>> future =
                kafkaByteTemplate.send(topic, message.getBytes(StandardCharsets.UTF_8));

        future.addCallback(new ListenableFutureCallback<SendResult<String, byte[]>>() {
            public void onFailure(Throwable throwable) {
                System.out.println("Unable to send message = ["+message+"] due to "+throwable.getMessage());
            }

            public void onSuccess(SendResult<String, byte[]> result) {
                System.out.println("[Thread]: "+Thread.currentThread().getId()+"\n"
                        +"Sent Message = ["+message+"] with offset = ["+result.getRecordMetadata().offset()+"]");
            }
        });
    }
}
