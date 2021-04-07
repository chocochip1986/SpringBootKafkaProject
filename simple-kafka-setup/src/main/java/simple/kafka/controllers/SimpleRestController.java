package simple.kafka.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import simple.kafka.producers.KafkaProducer;

@RestController
public class SimpleRestController {
    @Autowired
    private KafkaProducer producer;

    @GetMapping(value = "/v1/api/hitMe")
    public ResponseEntity<String> trigger() {
        producer.sendMessage("topic.one", "HAHAHAHAHHAHAHAH");
        return new ResponseEntity<>("SWEE LA", HttpStatus.OK);
    }
}
