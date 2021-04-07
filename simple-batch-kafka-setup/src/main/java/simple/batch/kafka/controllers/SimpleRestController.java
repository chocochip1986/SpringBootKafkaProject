package simple.batch.kafka.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import simple.batch.kafka.producers.KafkaByteProducer;
import simple.batch.kafka.producers.KafkaProducer;

import java.util.function.Consumer;

@RestController
public class SimpleRestController {
    @Autowired
    private KafkaProducer producer;

    @Autowired
    private KafkaByteProducer byteProducer;

    @GetMapping(value = "/v1/api/topic/{id}")
    public ResponseEntity<String> trigger(@PathVariable("id") String id) {
        if(id.equalsIgnoreCase("1")) {
            fireAway(t -> producer.sendMessage(t, "Message"), "topic.one");
        } else if (id.equalsIgnoreCase("2")) {
            fireAway(t -> byteProducer.sendMessage(t, "Message in Bytes YO"), "topic.two");
        }

        return new ResponseEntity<>("SWEE LA", HttpStatus.OK);
    }

    private void fireAway(Consumer<String> consumer, String topic) {
        for(int i = 0 ; i < 1000 ; i++) {
            consumer.accept(topic);
        }
    }
}
