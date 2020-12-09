package spring_kafka.kafka_producers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class KafkaByteProducer {
    @Autowired
    private KafkaTemplate<String, byte[]> kafkaByteTemplate;

    public void sendMessageWithReply(String topic, final byte[] message) {
        ListenableFuture<SendResult<String, byte[]>> future =
                kafkaByteTemplate.send(topic, message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, byte[]>>() {
            public void onFailure(Throwable throwable) {
                System.out.println("Unable to send message = ["+message+"] due to "+throwable.getMessage());
            }

            public void onSuccess(SendResult<String, byte[]> result) {
                System.out.println("[Thread]: "+Thread.currentThread().getId()+"\n"
                        +"Sent Message = ["+message+"]");
            }
        });
    }
}
