package simple.kafka.backoff.configs;

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
import org.springframework.kafka.listener.RetryingBatchErrorHandler;
import org.springframework.kafka.listener.SeekToCurrentBatchErrorHandler;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
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

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaByteListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerByteFactory());
        factory.setConcurrency(1);
        factory.setErrorHandler(eh());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaBatchByteListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerByteFactory());
        factory.setConcurrency(1);
        factory.setBatchListener(true);
        factory.setBatchErrorHandler(beh());
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
}
