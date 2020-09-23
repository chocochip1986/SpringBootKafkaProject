package spring_kafka.kafka_consumers;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import spring_kafka.constants.KafkaConstants;

@Service
public class KafkaBatchConsumer {

    @KafkaListener(topics = KafkaConstants.TOPIC_TWO, groupId = KafkaConstants.GROUP_ID_TWO, containerFactory = "kafkaBatchListenerContainerFactory")
    public void consume(String message) throws InterruptedException {
        String finalMsg = "[Consumer Thread]: "+Thread.currentThread().getId()+"\n";
        finalMsg += "[Message]: "+message;
        System.out.println(finalMsg);
    }
}
