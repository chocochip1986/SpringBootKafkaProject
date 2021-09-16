package rebalancing.consumers;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

@Service
public class KafkaByteConsumer {
    @KafkaListener(topics = "topic.two", containerFactory = "kafkaByteListenerContainerFactory")
    public void consume(ConsumerRecord<String, byte[]> record, Consumer<String, byte[]> consumer) {
        System.out.println("["+Thread.currentThread().getId()+"] Processing message from topic: "+record.topic()+", partition: "+record.partition()+", offset: "+record.offset());
        sleep();
        consumer.commitSync(new HashMap<>() {{
            put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset(), "commit"));
        }});
    }

    public void sleep() {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
