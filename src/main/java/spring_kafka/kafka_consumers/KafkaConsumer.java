package spring_kafka.kafka_consumers;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import spring_kafka.constants.KafkaConstants;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = KafkaConstants.TOPIC_ONE, groupId = KafkaConstants.GROUP_ID_ONE, containerFactory = "kafkaListenerContainerFactory")
    public void consume(String message) throws InterruptedException {
        String finalMsg = "[Thead]: "+Thread.currentThread().getId()+"\n";
        finalMsg += "[Message]: "+message;
        System.out.println(finalMsg);
    }
}
