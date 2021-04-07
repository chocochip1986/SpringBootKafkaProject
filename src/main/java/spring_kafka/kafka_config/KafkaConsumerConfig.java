package spring_kafka.kafka_config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.converter.BatchMessageConverter;
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.util.backoff.FixedBackOff;
import spring_kafka.constants.KafkaConstants;

import java.util.HashMap;
import java.util.Map;

//@EnableKafka annotation is required on the configuration class to enable detection of @KafkaListener annotation on spring managed beans:
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value(value = "${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapAddress;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    //The maximum parallelism of a group is that the number of consumers in the group ← no of partitions.
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstants.GROUP_ID_ONE);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-512");
//        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "/Users/guozheng/Desktop/guozheng/SpringBootKafkaProject/scripts/docker/client/client.truststore.jks");
//        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "password1234");
//        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, "/Users/guozheng/Desktop/guozheng/SpringBootKafkaProject/scripts/docker/client/client.keystore.jks");
//        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "password1234");
//        props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "password1234");
//        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
//        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        return new DefaultKafkaConsumerFactory<String, String>(props);
    }

    @Bean
    public ConsumerFactory<String, String> consumerPersonInfoFactory() {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstants.GROUP_ID_THREE);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-512");
//        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "/Users/guozheng/Desktop/guozheng/SpringBootKafkaProject/scripts/docker/client/client.truststore.jks");
//        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "password1234");
//        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, "/Users/guozheng/Desktop/guozheng/SpringBootKafkaProject/scripts/docker/client/client.keystore.jks");
//        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "password1234");
//        props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "password1234");
//        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
//        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        return new DefaultKafkaConsumerFactory<String, String>(props);
    }

    @Bean
    public ConsumerFactory<String, String> batchConsumerFactory() {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstants.GROUP_ID_TWO);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-512");
//        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "/Users/guozheng/Desktop/guozheng/SpringBootKafkaProject/scripts/docker/client/client.truststore.jks");
//        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "password1234");
//        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, "/Users/guozheng/Desktop/guozheng/SpringBootKafkaProject/scripts/docker/client/client.keystore.jks");
//        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "password1234");
//        props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "password1234");
//        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
//        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        return new DefaultKafkaConsumerFactory<String, String>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(10);
        return factory;
    }

    @Bean
    public SeekToCurrentErrorHandler errorHandler() {
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        (KafkaOperations<String, String>) kafkaTemplate,
                        (cr, ex) -> new TopicPartition("retry.topic", -1));
        return new SeekToCurrentErrorHandler(recoverer, new FixedBackOff(1, 2L));
    }

    //the setConcurrency method creates N number of KafkaMessageListenerContainer instances.
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaBatchListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(batchConsumerFactory());
        factory.setConcurrency(10);
        factory.setMessageConverter(batchMessageConverter());
        factory.setBatchListener(true); //Starting with version 1.1, you can configure @KafkaListener methods to receive the entire batch of consumer records received from the consumer poll.
        return factory;
    }

    @Bean
    public BatchMessageConverter batchMessageConverter() {
        BatchMessagingMessageConverter batchMessagingMessageConverter = new BatchMessagingMessageConverter();
        batchMessagingMessageConverter.setGenerateMessageId(true);
        return batchMessagingMessageConverter;
    }

    //Kafka Listeners who subscribe to this bean can use it to handle errors
    @Bean
    public KafkaListenerErrorHandler kafkaListenerErrorHandler() {
        KafkaListenerErrorHandler kafkaListenerErrorHandler = new KafkaListenerErrorHandler() {
            public Object handleError(Message<?> message, ListenerExecutionFailedException e) {
                System.out.println("[ERROR ON LISTENER]: "+e.getMessage()+" for message: "+message.getPayload().toString());
                return null;
            }
        };
        return kafkaListenerErrorHandler;
    }
}
