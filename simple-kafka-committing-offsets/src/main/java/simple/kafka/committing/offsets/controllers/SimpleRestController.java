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
        if(id.equalsIgnoreCase("1")) {
            producer.sendMessage("topic.one", "HAHAHAHAHHAHAHAH");
        } else if (id.equalsIgnoreCase("2")) {
            for ( int i = 0 ; i < 10 ; i++ ) {
                byteProducer.sendMessage("topic.two", "This is message no. "+i);
            }
        } else if (id.equalsIgnoreCase("3")) {
            try {
                byteProducer.sendMessage("topic.three", Person.builder().name("NAME HAHHAHA").build());
            } catch (JsonProcessingException e) {
                return new ResponseEntity<>("NO SWEE LEH", HttpStatus.BAD_REQUEST);
            }
        } else if (id.equalsIgnoreCase("4")) {
            for ( int i = 0 ; i < 10 ; i++ ) {
                byteProducer.sendMessage("topic.four", "This is message no. "+i);
            }
        } else if (id.equalsIgnoreCase("5")) {
            for ( int i = 0 ; i < 10 ; i++ ) {
                byteProducer.sendMessage("topic.five", "This is message no. "+i);
            }
        }
        return new ResponseEntity<>("SWEE LA", HttpStatus.OK);
    }
}
