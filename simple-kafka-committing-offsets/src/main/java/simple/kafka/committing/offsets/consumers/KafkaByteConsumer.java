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

    //If you do not manually acknowledge the messages, they will be redelivered to the consumer the next time the consumer/applicatino starts up
    //If you don't call acknowledge(), and exit normally, the committed offset won't be moved, but the position will; you will get the next records on the next poll().
    //
    //If you restart the application, you will get the same record(s) redelivered.
    //If you throw an exception instead of negatively acknowledging, it'll trigger the SeekToCurrentErrorHandler to be invoked and thus the offending
    //message will be acknowledged and consumption of the remaining messages in the poll will continue.
    //Negatively acknowledging a message is doing a Thread.sleep(milliseconds) under the hood

    /*
    With a record listener, when nack() is called, any pending offsets are committed, the remaining records from the last poll are discarded,
    and seeks are performed on their partitions so that the failed record and unprocessed records are redelivered on the next poll().
    The consumer thread can be paused before redelivery, by setting the sleep argument.
    This is similar functionality to throwing an exception when the container is configured with a SeekToCurrentErrorHandler
     */
    @KafkaListener(topics = "topic.four", containerFactory = "kafkaManualAckByteListenerContainerFactory")
    public void consumeManualAck(ConsumerRecord<String, byte[]> record, Acknowledgment acknowledgment) {
        String str = convert(record.value());
        if ( str.matches("^.*no\\. 5$") ) {
            acknowledgment.nack(5000L);
//            throw new RuntimeException("CANNOT LA");
        } else {
            System.out.println("Message consumed: "+str);
        }
        acknowledgment.acknowledge();
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
            if ( str.matches("^.*no\\. 5$") ) {
                throw new BatchListenerFailedException("CANNOT LA", record);
            } else {
                System.out.println("Message consumed "+convert(record.value()));
            }
            System.out.println("Message consumed "+str);
            acknowledgment.acknowledge();
        }
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
