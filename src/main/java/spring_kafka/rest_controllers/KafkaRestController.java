package spring_kafka.rest_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import spring_kafka.constants.KafkaConstants;
import spring_kafka.kafka_producers.KafkaPersonInfoProducer;
import spring_kafka.kafka_producers.KafkaProducer;

@RestController
public class KafkaRestController {
    @Autowired
    KafkaProducer kafkaProducer;

    @Autowired
    KafkaPersonInfoProducer kafkaPersonInfoProducer;

    @GetMapping(value = "/sendMsg/{id}")
    public ResponseEntity<String> sendMessage(@PathVariable("id") int id) {
        switch (id) {
            case 1:
                spamThatMessageTo(KafkaConstants.TOPIC_ONE, "Message sent for topic 1!");
                break;
            case 2:
                spamThatMessageTo(KafkaConstants.TOPIC_TWO, "Message sent for topic 1!");
                break;
            case 3:
                spamThatMessageTo(KafkaConstants.TOPIC_THREE, "aefaw");
                break;
            default:
                System.out.println("Nothing sent!");
        }
        return new ResponseEntity<String>("Hello World!", HttpStatus.OK);
    }

    private void spamThatMessageTo(String topic, String message) {
        for ( int i = 0 ; i < 100 ; i++ ) {
            kafkaProducer.sendMessageWithReply(topic, "["+i+"] "+message);
        }
    }
}
