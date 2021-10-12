package simple.kafka.retry.consumers;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class KafkaByteConsumer {
    @KafkaListener(topics = "topic.two", containerFactory = "kafkaByteListenerContainerFactory")
    public void consume(ConsumerRecord<String, byte[]> record) {
        String str = convert(record.value());
//        if ( str.matches("^.*no\\. 5$") ) {
//            throw new RuntimeException("CANNOT LA");
//        } else {
            System.out.println("Message consumed: "+convert(record.value()));
//        }
    }

    @KafkaListener(topics = "topic.four", containerFactory = "kafkaBatchByteListenerContainerFactory")
    public void consume(List<ConsumerRecord<String, byte[]>> records) {
        System.out.println("Incoming messages of size "+records.size());
        for (ConsumerRecord<String, byte[]> record: records) {
            String str = convert(record.value());
//            if ( str.matches("^.*no\\. 5$") ) {
//                throw new BatchListenerFailedException("CANNOT LA", record);
//            } else {
//                System.out.println("Message consumed "+convert(record.value()));
//            }
            System.out.println("Message consumed "+convert(record.value()));
        }
    }

    @KafkaListener(topics = "topic.five", containerFactory = "kafkaBatchRetryByteListenerContainerFactory")
    public void consumeWithRetry(List<ConsumerRecord<String, byte[]>> records) {
        System.out.println("Incoming messages of size "+records.size());
        for (ConsumerRecord<String, byte[]> record: records) {
            String str = convert(record.value());
            if ( str.matches("^.*no\\. 5$") ) {
                throw new BatchListenerFailedException("CANNOT LA", record);
//                throw new RuntimeException("CANNOT LA");
            } else {
                System.out.println("Message consumed "+convert(record.value()));
            }
//            System.out.println("Message consumed "+str);
        }
    }

    private String convert(byte[] payload) {
        return new String(payload, StandardCharsets.UTF_8);
    }
}
