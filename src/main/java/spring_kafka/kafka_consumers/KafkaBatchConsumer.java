package spring_kafka.kafka_consumers;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import spring_kafka.constants.KafkaConstants;

import java.util.List;

@Service
public class KafkaBatchConsumer {
    @KafkaListener(topics = KafkaConstants.TOPIC_TWO, groupId = KafkaConstants.GROUP_ID_TWO, containerFactory = "kafkaBatchListenerContainerFactory")
    public void consume(List<ConsumerRecord<String, String>> records) throws InterruptedException {
        String finalMsg = "[Consumer Thread]: "+Thread.currentThread().getId()+"\n[Number of Messages]: "+records.size()+"\n";
        for( ConsumerRecord<String, String> record : records ) {
            finalMsg += getHeaders(record);
            finalMsg += "[Message]: "+record.value()+"\n\n";
            System.out.println(finalMsg);
        }
    }

    private String getHeaders(ConsumerRecord<String, String> record) {
        return "[ConsumerRecord Header]: "+record.headers().toString()+"\n";
    }

    private String getHeaders(Message<String> message) {
        return "[Message Header]: "+message.getHeaders().toString()+"\n";
    }
}
