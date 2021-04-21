package multi.thread.consumption.consumers;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class KafkaByteConsumer {
    @KafkaListener(topics = "topic.two", containerFactory = "kafkaByteListenerContainerFactory")
    public void consume(List<ConsumerRecord<String, byte[]>> records, Acknowledgment acknowledgment) {
        System.out.println("Batch Size: "+ records.size());
        for(int i = 0 ; i < records.size() ; i++ ) {
            if ( (new String(records.get(i).value(), StandardCharsets.UTF_8)).equals("5")) {
                acknowledgment.nack(i, 2L);
                throw new RuntimeException("WAH LAN EH CUI LA");
            }
            System.out.println("Message consumed: "+new String(records.get(i).value(), StandardCharsets.UTF_8));
            acknowledgment.acknowledge();
        }
    }
}
