package spring_kafka.kafka_consumers;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import spring_kafka.constants.KafkaConstants;

import java.util.List;

@Service
public class KafkaBatchConsumer {

    @KafkaListener(topics = KafkaConstants.TOPIC_TWO, groupId = KafkaConstants.GROUP_ID_TWO, containerFactory = "kafkaBatchListenerContainerFactory")
    public void consume(List<Message<String>> messages) throws InterruptedException {
        String finalMsg = "[Consumer Thread]: "+Thread.currentThread().getId()+"\n";
        for( Message<String> message : messages ) {
            finalMsg += getHeaders(message);
            finalMsg += "[Message]: "+message.getPayload()+"\n\n";
            System.out.println(finalMsg);
        }
    }

    private String getHeaders(Message<String> message) {
        return "[Message Header]: "+message.getHeaders().toString()+"\n";
    }
}
