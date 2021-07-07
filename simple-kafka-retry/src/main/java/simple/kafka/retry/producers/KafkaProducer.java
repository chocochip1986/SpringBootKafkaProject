package simple.kafka.retry.producers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class KafkaProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    public void sendMessageWithReply(String topic, final String message) {
        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(topic, message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            public void onFailure(Throwable throwable) {
                System.out.println("Unable to send message = ["+message+"] due to "+throwable.getMessage());
            }

            public void onSuccess(SendResult<String, String> result) {
                System.out.println("[Thread]: "+Thread.currentThread().getId()+"\n"
                        +"Sent Message = ["+message+"] with offset = ["+result.getRecordMetadata().offset()+"]");
            }
        });
    }
}
