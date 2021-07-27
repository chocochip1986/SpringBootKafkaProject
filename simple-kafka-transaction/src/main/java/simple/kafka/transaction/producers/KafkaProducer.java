package simple.kafka.transaction.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class KafkaProducer {
    @Autowired private KafkaTemplate<String, byte[]> kafkaTemplate;
    @Autowired private ObjectMapper objectMapper;

    public void sendMessage(String topic, byte[] message) {
        kafkaTemplate.send(topic, message);
    }

    public void sendMessage(String topic, Object message) {
        kafkaTemplate.send(topic, convert(message));
    }

    public void sendMessageWithReply(String topic, final byte[] message) {
        ListenableFuture<SendResult<String, byte[]>> future =
                kafkaTemplate.send(topic, message);

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

    private byte[] convert(Object obj) {
        try {
            return this.objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to convert "+obj.getClass()+" to byte[]!", e);
        }
    }
}
