package spring_kafka.rest_controllers;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import spring_kafka.constants.KafkaConstants;
import spring_kafka.kafka_producers.KafkaByteProducer;
import spring_kafka.kafka_producers.KafkaPersonInfoProducer;
import spring_kafka.kafka_producers.KafkaProducer;
import spring_kafka.utilities.PhakerNric;

import java.io.UnsupportedEncodingException;

@RestController
public class KafkaRestController {
    @Autowired
    KafkaProducer kafkaProducer;

    @Autowired
    KafkaPersonInfoProducer kafkaPersonInfoProducer;

    @Autowired
    KafkaByteProducer kafkaByteProducer;

    private PhakerNric phakerNric = PhakerNric.instance();

    @GetMapping(value = "/sendMsg/{id}")
    public ResponseEntity<String> sendMessage(@PathVariable("id") int id) throws JSONException, UnsupportedEncodingException {
        switch (id) {
            case 1:
                spamThatMessageTo(KafkaConstants.TOPIC_ONE, "Message sent for topic 1!");
                break;
            case 2:
                spamThatMessageTo(KafkaConstants.TOPIC_TWO, "Message sent for topic 2!");
                break;
            case 3:
                spamThatMessageTo(KafkaConstants.TOPIC_THREE, "Message sent for topic 3!");
                break;
            case 4:
                JSONObject jsonObject = new JSONObject("{\"ENTITY_KEY\": \"3195195\", \"NATURAL_ID\": \"T5438518D\", \"PERSON_ID_TYPE\": \"NRIC\", \"NAME\": \"BOYBOY 1\", \"CITIZENSHIP_ATTAINMENT_DATE\": \"1992-10-31T16:00:00.000Z\", \"TYPE\": \"SINGAPORE_CITIZEN\", \"NATIONALITY_VALID_FROM\": \"1992-10-31T16:00:00.000Z\" }");
                this.kafkaByteProducer.sendMessageWithReply("datasource-person-create", jsonObject.toString().getBytes("UTF-8"));
            default:
                System.out.println("Nothing sent!");
        }
        return new ResponseEntity<String>("Hello World!", HttpStatus.OK);
    }

    @GetMapping(value = "/fireaway")
    public ResponseEntity<String> sendKafkaMessages() throws JSONException {
        try {
            for ( int i = 0 ; i < 100000 ; i++ ) {
                JSONObject jsonObject = new JSONObject("{\"ENTITY_KEY\": \""+ i +"\", \"NATURAL_ID\": \""+phakerNric.followNric()+"\", \"PERSON_ID_TYPE\": \"NRIC\", \"NAME\": \"BOYBOY "+i+"\", \"CITIZENSHIP_ATTAINMENT_DATE\": \"1992-10-31T16:00:00.000Z\", \"TYPE\": \"SINGAPORE_CITIZEN\", \"NATIONALITY_VALID_FROM\": \"1992-10-31T16:00:00.000Z\" }");
                this.kafkaByteProducer.sendMessageWithReply("datasource-person-create", jsonObject.toString().getBytes("UTF-8"));
            }
            return new ResponseEntity<>("", HttpStatus.OK);
        } catch ( JSONException | UnsupportedEncodingException e ) {
            return new ResponseEntity<>("NOT OK!", HttpStatus.BAD_REQUEST);
        }
    }

    private void spamThatMessageTo(String topic, String message) {
        for ( int i = 0 ; i < 100 ; i++ ) {
            kafkaProducer.sendMessageWithReply(topic, "["+i+"] "+message);
        }
    }
}
