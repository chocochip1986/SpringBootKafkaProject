package simple.kafka.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import simple.kafka.entities.Bar;
import simple.kafka.entities.Foo;
import simple.kafka.entities.FooOrBar;
import simple.kafka.producers.KafkaByteProducer;
import simple.kafka.producers.KafkaProducer;
import spring.kafka.commons.entities.Person;

@RestController
public class SimpleRestController {
    private int count = 0;
    @Autowired
    private KafkaProducer producer;

    @Autowired
    private KafkaByteProducer byteProducer;

    @GetMapping(value = "/v1/api/topic/{id}")
    public ResponseEntity<String> trigger(@PathVariable("id") String id) throws JsonProcessingException {
        if(id.equalsIgnoreCase("1")) {
            producer.sendMessage("topic.one", "HAHAHAHAHHAHAHAH");
        } else if (id.equalsIgnoreCase("2")) {
            send();
//            byteProducer.sendMessage("topic.two", "In Bytes YO");
        } else if (id.equalsIgnoreCase("3")) {
            try {
                byteProducer.sendMessage("topic.three", Person.builder().name("NAME HAHHAHA").build());
            } catch (JsonProcessingException e) {
                return new ResponseEntity<>("NO SWEE LEH", HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>("SWEE LA", HttpStatus.OK);
    }

    private void send() throws JsonProcessingException {
        Foo foo = new Foo("FOO");
        Bar bar = new Bar("BAR", "BAR");
        ObjectMapper objectMapper = new ObjectMapper();

        byte[] payload;
        if ( count++ % 2 == 0 ) {
            payload = objectMapper.writeValueAsBytes(foo);
        } else {
            payload = objectMapper.writeValueAsBytes(bar);
        }
        byte[] newPayload = new byte[payload.length+1];

        System.arraycopy(payload, 0, newPayload, 1, payload.length);
        newPayload[0] = 48;
        byteProducer.sendMessage("topic.two", newPayload);
    }
}
