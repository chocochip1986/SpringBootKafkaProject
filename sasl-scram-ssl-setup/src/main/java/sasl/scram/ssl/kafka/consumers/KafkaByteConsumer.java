package sasl.scram.ssl.kafka.consumers;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class KafkaByteConsumer {

//    In Batch Listeners, the List<...> records must be the only input argument. You can, however, have an optional Acknowledgement input argument.
    @KafkaListener(topics = "topic.two", containerFactory = "kafkaByteListenerContainerFactory")
    public void consume(List<ConsumerRecord<String, byte[]>> records) {
        System.out.println("Record Batch Size: "+records.size());
        for (ConsumerRecord<String, byte[]> record: records) {
            System.out.println("Message consumed: "+new String(record.value(), StandardCharsets.UTF_8));
        }
    }
}
