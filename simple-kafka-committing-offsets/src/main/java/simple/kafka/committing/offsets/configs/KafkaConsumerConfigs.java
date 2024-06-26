package simple.kafka.committing.offsets.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.RetryingBatchErrorHandler;
import org.springframework.kafka.listener.SeekToCurrentBatchErrorHandler;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfigs {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group.one");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConsumerFactory<String, byte[]> consumerByteFactory() {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group.two");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    /*
    Note: The following lists describes the action taken by the container for each AckMode (when transactions are not being used).
    When using transactions, the offset(s) are sent to the transaction and the semantics are equivalent to RECORD or BATCH, depending on the listener type (record or batch).

    RECORD: Commit the offset when the listener returns after processing the record
    BATCH (default): ( Commit the offset when all the records returned by the poll() have been processed. )
    MANUAL: The message listener is responsible to acknowledge() the Acknowledgment. After that, the same semantics as BATCH are applied.
    MANUAL_IMMEDIATE: Commit the offset immediately when the Acknowledgment.acknowledge() method is called by the listener.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaRecordAckByteListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerByteFactory());
        factory.setConcurrency(1);
        factory.setErrorHandler(eh());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaBatchAckByteListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerByteFactory());
        factory.setConcurrency(1);
        factory.setErrorHandler(eh());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaManualAckByteListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerByteFactory());
        factory.setConcurrency(1);
        factory.setErrorHandler(eh());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaManualImmDAckByteListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerByteFactory());
        factory.setConcurrency(1);
        factory.setErrorHandler(eh());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    //You cannot use AckMode.RECORD on a batch listener
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaBatchBatchByteListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerByteFactory());
        factory.setConcurrency(1);
        factory.setBatchListener(true);
        factory.setBatchErrorHandler(beh());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaManualAckBatchByteListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerByteFactory());
        factory.setConcurrency(1);
        factory.setBatchListener(true);
        factory.setBatchErrorHandler(beh());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaBatchRetryByteListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerByteFactory());
        factory.setConcurrency(1);
        factory.setBatchListener(true);
        factory.setBatchErrorHandler(rbeh());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaByteWithConverterListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerByteFactory());
        factory.setConcurrency(1);
        factory.setMessageConverter(new ByteArrayJsonMessageConverter(objectMapper));
        factory.setErrorHandler(eh());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(1);
        factory.setErrorHandler(eh());
        return factory;
    }

    @Bean
    public SeekToCurrentErrorHandler eh() {
        return new SeekToCurrentErrorHandler((record, e) -> {
            //After the BackOff is exhausted, this BiConsumer will be executed so you can do your recovery here or you can do other stuff.
            System.out.println("Record died");
        }, new FixedBackOff(0L, 1L));
    }

    /*
    The SeekToCurrentBatchErrorHandler has no mechanism to recover after a certain number of failures.
    One reason for this is there is no guarantee that, when a batch is redelivered,
    the batch has the same number of records and/or the redelivered records are in the same order.
    It is impossible, therefore, to maintain retry state for a batch. If you use this, expect to get infinite retries
     */
    @Bean
    public SeekToCurrentBatchErrorHandler beh() {
        SeekToCurrentBatchErrorHandler beh = new SeekToCurrentBatchErrorHandler();
        beh.setBackOff(new FixedBackOff(0L, 1L));
        return beh;
    }

    /*
    This is now the default error handler for batch listeners.
    The retries are performed from the in-memory batch of records.
    In order to avoid a rebalance during an extended retry sequence, the error handler pauses the consumer, polls it before sleeping for the back off, for each retry, and calls the listener again.
    If/when retries are exhausted, the ConsumerRecordRecoverer is called for each record in the batch.
    If the recoverer throws an exception, or the thread is interrupted during its sleep, a SeekToCurrentErrorHandler is invoked so that the batch of records will be redelivered on the next poll. Before exiting, regardless of the outcome, the consumer is resumed.
    The default configuration retries 9 times (10 delivery attempts) with no back off between deliveries.
    This error handler works in conjunction with the listener throwing a BatchListenerFailedException providing the index in the batch where the failure occurred (or the failed record itself).
    If the listener throws a different exception, or the index is out of range, the error handler falls back to invoking a SeekToCurrentBatchErrorHandler and
    the whole batch is retried, with no recovery available. The sequence of events is:
    1. Commit the offsets of the records before the index.
    2. If retries are not exhausted, perform seeks so that all the remaining records (including the failed record) will be redelivered.
    3. If retries are exhausted, attempt recovery of the failed record (default log only) and perform seeks so that the remaining records (excluding the failed record) will be redelivered. The recovered record’s offset is committed
    4. If retries are exhausted and recovery fails, seeks are performed as if retries are not exhausted.
     */
    @Bean
    public RetryingBatchErrorHandler rbeh() {
        return new RetryingBatchErrorHandler(new FixedBackOff(0L, 1L), new CustomRecoverer());
    }
}
