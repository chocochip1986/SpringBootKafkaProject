package simple.kafka.transaction.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import simple.kafka.transaction.jpa.AnimalEntity;
import simple.kafka.transaction.producers.KafkaByteProducer;
import simple.kafka.transaction.producers.KafkaProducer;

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
        } else if (id.equalsIgnoreCase("3")) {
            try {
                byteProducer.sendMessage("topic.three", AnimalEntity.builder().name("NAME HAHHAHA").build());
            } catch (JsonProcessingException e) {
                return new ResponseEntity<>("NO SWEE LEH", HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>("SWEE LA", HttpStatus.OK);
    }
}
