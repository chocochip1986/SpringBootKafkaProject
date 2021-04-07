package simple.kafka.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import simple.kafka.producers.KafkaByteProducer;
import simple.kafka.producers.KafkaProducer;

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
            byteProducer.sendMessage("topic.two", "In Bytes YO");
        }

        return new ResponseEntity<>("SWEE LA", HttpStatus.OK);
    }
}
