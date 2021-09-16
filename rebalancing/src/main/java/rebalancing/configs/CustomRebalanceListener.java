package rebalancing.configs;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CustomRebalanceListener<K,V> implements ConsumerRebalanceListener {
//    private KafkaConsumer<K,V> consumer;
//    public CustomRebalanceListener(KafkaConsumer<K,V> consumer) {
//        this.consumer = consumer;
//    }
//    @Override
//    public void onPartitionsRevokedBeforeCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
//        System.out.println("Revoked before commit");
//        ConsumerAwareRebalanceListener.super.onPartitionsRevokedBeforeCommit(consumer, partitions);
//    }
//
//    @Override
//    public void onPartitionsRevokedAfterCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
//        System.out.println("Revoked after commit");
//        ConsumerAwareRebalanceListener.super.onPartitionsRevokedAfterCommit(consumer, partitions);
//    }
//
//    @Override
//    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
//        System.out.println("On Revocation");
//        ConsumerAwareRebalanceListener.super.onPartitionsRevoked(partitions);
//    }

    private Map<TopicPartition, OffsetAndMetadata> generate(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
        Map<TopicPartition, OffsetAndMetadata> offsetsToCommit = new HashMap<>();
        Iterator<TopicPartition> itr = partitions.iterator();

        while (itr.hasNext()) {
            TopicPartition topicPartition = itr.next();

            for(int i = 0 ; i < partitions.size() ; i++ ) {
                try {
                    if(!offsetsToCommit.containsKey(topicPartition)) {
                        offsetsToCommit.put(topicPartition, new OffsetAndMetadata(consumer.position(topicPartition)));
                    }
                } catch (RuntimeException e) {
                    System.out.println("Exception occurred before revoking");
                    return null;
                }
            }
        }
        return offsetsToCommit;
    }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> collection) {

    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> collection) {

    }

    @Override
    public void onPartitionsLost(Collection<TopicPartition> partitions) {
        ConsumerRebalanceListener.super.onPartitionsLost(partitions);
    }
}
