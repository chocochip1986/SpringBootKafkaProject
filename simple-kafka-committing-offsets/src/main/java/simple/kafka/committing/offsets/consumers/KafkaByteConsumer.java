package simple.kafka.committing.offsets.consumers;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class KafkaByteConsumer {
    @KafkaListener(topics = "topic.two", containerFactory = "kafkaRecordAckByteListenerContainerFactory")
    public void consumeRecordAck(ConsumerRecord<String, byte[]> record) {
        String str = convert(record.value());
        if ( str.matches("^.*no\\. 5$") ) {
            throw new RuntimeException("CANNOT LA");
        } else {
            System.out.println("Message consumed: "+str);
        }
    }

    @KafkaListener(topics = "topic.three", containerFactory = "kafkaBatchAckByteListenerContainerFactory")
    public void consumeBatchAck(ConsumerRecord<String, byte[]> record) {
        String str = convert(record.value());
        if ( str.matches("^.*no\\. 5$") ) {
            throw new RuntimeException("CANNOT LA");
        } else {
            System.out.println("Message consumed: "+str);
        }
    }

    @KafkaListener(topics = "topic.four", containerFactory = "kafkaManualAckByteListenerContainerFactory")
    public void consumeManualAck(ConsumerRecord<String, byte[]> record, Acknowledgment acknowledgment) {
        String str = convert(record.value());
        if ( str.matches("^.*no\\. 5$") ) {
//            acknowledgment.nack(1L);
//            throw new RuntimeException("CANNOT LA");
        } else {
            System.out.println("Message consumed: "+str);
        }
    }

    @KafkaListener(topics = "topic.five", containerFactory = "kafkaManualImmDAckByteListenerContainerFactory")
    public void consumeManualImmediateAck(ConsumerRecord<String, byte[]> record, Acknowledgment acknowledgment) {
        String str = convert(record.value());
        if ( str.matches("^.*no\\. 5$") ) {
//            acknowledgment.nack(1L);
//            throw new RuntimeException("CANNOT LA");
        } else {
            System.out.println("Message consumed: "+str);
        }
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "topic.six", containerFactory = "kafkaBatchBatchByteListenerContainerFactory")
    public void consume(List<ConsumerRecord<String, byte[]>> records) {
        System.out.println("Incoming messages of size "+records.size());
        for (ConsumerRecord<String, byte[]> record: records) {
            String str = convert(record.value());
//            if ( str.matches("^.*no\\. 5$") ) {
//                throw new BatchListenerFailedException("CANNOT LA", record);
//            } else {
//                System.out.println("Message consumed "+convert(record.value()));
//            }
            System.out.println("Message consumed "+str);
        }
    }

    @KafkaListener(topics = "topic.seven", containerFactory = "kafkaManualAckBatchByteListenerContainerFactory")
    public void consumeBatchManualAck(List<ConsumerRecord<String, byte[]>> records, Acknowledgment acknowledgment) {
        System.out.println("Incoming messages of size "+records.size());
        for (ConsumerRecord<String, byte[]> record: records) {
            String str = convert(record.value());
//            if ( str.matches("^.*no\\. 5$") ) {
//                throw new BatchListenerFailedException("CANNOT LA", record);
//            } else {
//                System.out.println("Message consumed "+convert(record.value()));
//            }
            System.out.println("Message consumed "+str);
        }
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "topic.eight", containerFactory = "kafkaBatchRetryByteListenerContainerFactory")
    public void consumeWithRetry(List<ConsumerRecord<String, byte[]>> records) {
        System.out.println("Incoming messages of size "+records.size());
        for (ConsumerRecord<String, byte[]> record: records) {
            String str = convert(record.value());
            if ( str.matches("^.*no\\. 5$") ) {
                throw new BatchListenerFailedException("CANNOT LA", record);
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
