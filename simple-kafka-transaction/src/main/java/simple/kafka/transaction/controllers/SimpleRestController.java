package simple.kafka.transaction.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import simple.kafka.transaction.dto.DtoOne;
import simple.kafka.transaction.jpa.AnimalEntity;
import simple.kafka.transaction.producers.KafkaByteProducer;
import simple.kafka.transaction.producers.KafkaProducer;
import simple.kafka.transaction.producers.TxKafkaByteProducer;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
public class SimpleRestController {
    @Autowired private KafkaProducer producer;

    @Autowired private KafkaByteProducer byteProducer;

    @Autowired private TxKafkaByteProducer txKafkaByteProducer;

    @GetMapping(value = "/v1/api/topic/{id}")
    public ResponseEntity<String> trigger(@PathVariable("id") String id) {
        if( id.equalsIgnoreCase("1")) {
            //Producer initiated transaction
            txKafkaByteProducer.sendTxMessage("topic.one", DtoOne.builder().uuid(UUID.randomUUID().toString()).build());
        }

        return new ResponseEntity<>("SWEE LA", HttpStatus.OK);
    }
}
