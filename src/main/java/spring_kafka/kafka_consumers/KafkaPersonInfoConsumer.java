package spring_kafka.kafka_consumers;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import spring_kafka.constants.KafkaConstants;
import spring_kafka.entities.PersonInfo;

import java.util.List;

@Service
public class KafkaPersonInfoConsumer {
    @KafkaListener(topics = KafkaConstants.TOPIC_THREE,
            groupId = KafkaConstants.GROUP_ID_THREE,
            containerFactory = "consumerPersonInfoFactory",
            errorHandler = "kafkaListenerErrorHandler")
    public void consume(List<Message<PersonInfo>> messages) {
//        String finalMsg = "[Consumer Thread]: "+Thread.currentThread().getId()+"\n";
//        finalMsg += "[Message]: "+message;
//        System.out.println(finalMsg);
    }
}
