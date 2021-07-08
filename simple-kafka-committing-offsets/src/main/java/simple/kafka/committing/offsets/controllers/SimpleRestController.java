package simple.kafka.committing.offsets.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import simple.kafka.committing.offsets.producers.KafkaByteProducer;
import simple.kafka.committing.offsets.producers.KafkaProducer;
import spring.kafka.commons.entities.Person;

@RestController
public class SimpleRestController {
    @Autowired
    private KafkaProducer producer;

    @Autowired
    private KafkaByteProducer byteProducer;

    @GetMapping(value = "/v1/api/topic/{id}")
    public ResponseEntity<String> trigger(@PathVariable("id") String id) {
        switch (id) {
            case "1":
                sendMultiple("topic.one");
                break;
            case "2":
                sendMultiple("topic.two");
                break;
            case "3":
                sendMultiple("topic.three");
                break;
            case "4":
                sendMultiple("topic.four");
                break;
            case "5":
                sendMultiple("topic.five");
                break;
            case "6":
                sendMultiple("topic.six");
                break;
            case "7":
                sendMultiple("topic.seven");
                break;
            case "8":
                sendMultiple("topic.eight");
                break;
            default:
        }
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    private void sendMultiple(String topic) {
        for ( int i = 0 ; i < 10 ; i++ ) {
            byteProducer.sendMessage(topic, "This is message no. "+i);
        }
    }
}
