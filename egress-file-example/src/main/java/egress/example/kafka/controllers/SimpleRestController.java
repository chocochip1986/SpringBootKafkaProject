package egress.example.kafka.controllers;

import egress.example.kafka.producers.KafkaByteProducer;
import egress.example.kafka.producers.KafkaProducer;
import egress.example.kafka.services.SimpleService;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import spring.kafka.commons.entities.Person;

@RestController
public class SimpleRestController {
    @Autowired
    private KafkaProducer producer;

    @Autowired
    private KafkaByteProducer byteProducer;

    @Autowired private SimpleService simpleService;

    @Autowired private SessionFactory sessionFactory;

    @GetMapping(value = "/v1/api/topic/{id}")
    public ResponseEntity<String> trigger(@PathVariable("id") String id) {
        if(id.equalsIgnoreCase("1")) {
            producer.sendMessage("topic.one", "HAHAHAHAHHAHAHAH");
        } else if (id.equalsIgnoreCase("2")) {
            for ( int i = 0 ; i < 10 ; i++ ) {
                byteProducer.sendMessage("topic.two", "This is message no. "+i);
            }
        } else if (id.equalsIgnoreCase("3")) {
            byteProducer.sendMessage("topic.three", Person.builder().name("NAME HAHHAHA").build());
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

    @GetMapping(value = "hit")
    public ResponseEntity<String> trigger() {
        simpleService.init();
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
