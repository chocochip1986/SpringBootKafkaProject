package simple.kafka.transaction.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultAfterRollbackProcessor;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;
import org.springframework.kafka.transaction.ChainedKafkaTransactionManager;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfigs {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, byte[]> txKafkaByteTemplate;

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
    public ConsumerFactory<String, byte[]> txConsumerByteFactory() {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group.three");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 30000); //30 seconds for testing rebalancing
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public KafkaTransactionManager<String, byte[]> kafkaTransactionManager(ProducerFactory<String, byte[]> transactionalProducerByteFactory) {
        KafkaTransactionManager<String, byte[]> kafkaTransactionManager = new KafkaTransactionManager<>(transactionalProducerByteFactory);
        kafkaTransactionManager.setTransactionSynchronization(AbstractPlatformTransactionManager.SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
        return kafkaTransactionManager;
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager();
    }

    @Bean
    public ChainedKafkaTransactionManager<String, byte[]> chainedKafkaTransactionManager(JpaTransactionManager jpaTransactionManager, KafkaTransactionManager<String, byte[]> kafkaTransactionManager) {
        return new ChainedKafkaTransactionManager<>(jpaTransactionManager, kafkaTransactionManager);
    }

    /*
    When transactions are being used, no error handlers are configured, by default, so that the exception will roll back the transaction.
    Error handling for transactional containers are handled by the AfterRollbackProcessor.
    If you provide a custom error handler when using transactions, it must throw an exception if you want the transaction rolled back.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> txKafkaByteListenerContainerFactory(ProducerFactory<String, byte[]> transactionalProducerByteFactory) {
        DefaultAfterRollbackProcessor<String, byte[]> afterRollbackProcessor =
                new DefaultAfterRollbackProcessor<>(null, new FixedBackOff(0L, 0L), txKafkaByteTemplate, true);
        ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(txConsumerByteFactory());
        factory.setConcurrency(1);
//        factory.setErrorHandler(eh()); //If you plan to use transactional consumer, then you shouldn't have an error handler so that the error will be caught and fired up the chain

        factory.setAfterRollbackProcessor(afterRollbackProcessor);
        factory.getContainerProperties().setTransactionManager(kafkaTransactionManager(transactionalProducerByteFactory));
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        factory.getContainerProperties().setSyncCommits(true);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaByteListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerByteFactory());
        factory.setConcurrency(1);
        factory.setErrorHandler(eh());
        factory.getContainerProperties().setSyncCommits(true);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaByteWithConverterListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerByteFactory());
        factory.setConcurrency(1);
        factory.setMessageConverter(new ByteArrayJsonMessageConverter(objectMapper));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(1);
        return factory;
    }

    @Bean
    public SeekToCurrentErrorHandler eh() {
        return new SeekToCurrentErrorHandler((record, e) -> {
            //After the BackOff is exhausted, this BiConsumer will be executed so you can do your recovery here or you can do other stuff.
            System.out.println("Record died");
        }, new FixedBackOff(0L, 0L));
    }
}
