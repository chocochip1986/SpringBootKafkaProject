package simple.kafka.recovery.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import simple.kafka.recovery.producers.KafkaByteProducer;
import simple.kafka.recovery.producers.KafkaProducer;
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
            for( int i = 0 ; i < 10 ; i++ ) {
                producer.sendMessage("topic.one", String.valueOf(i));
            }
        } else if (id.equalsIgnoreCase("2")) {
            byteProducer.sendMessage("topic.two", "In Bytes YO");
        } else if (id.equalsIgnoreCase("3")) {
            try {
                byteProducer.sendMessage("topic.three", Person.builder().name("NAME HAHHAHA").build());
            } catch (JsonProcessingException e) {
                return new ResponseEntity<>("NO SWEE LEH", HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>("SWEE LA", HttpStatus.OK);
    }

    @GetMapping(value = "/v1/api/topic/{id}")
    public ResponseEntity<String> triggerForLoop(@PathVariable("id") String id) {
        for( int i = 0 ; i < 10 ; i++ ) {
            if(id.equalsIgnoreCase("1")) {
                producer.sendMessage("topic.one", String.valueOf(i));
            } else if (id.equalsIgnoreCase("2")) {
                byteProducer.sendMessage("topic.two", String.valueOf(i));
            } else if (id.equalsIgnoreCase("3")) {
                try {
                    byteProducer.sendMessage("topic.three", Person.builder().name("NAME "+String.valueOf(i)).build());
                } catch (JsonProcessingException e) {
                    return new ResponseEntity<>("NO SWEE LEH", HttpStatus.BAD_REQUEST);
                }
            }
        }
        return new ResponseEntity<>("SWEE LA", HttpStatus.OK);
    }
}
