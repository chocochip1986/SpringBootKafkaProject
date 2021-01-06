package spring_kafka.kafka_config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.support.converter.BatchMessageConverter;
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter;
import org.springframework.messaging.Message;
import spring_kafka.constants.KafkaConstants;

import java.util.HashMap;
import java.util.Map;

//@EnableKafka annotation is required on the configuration class to enable detection of @KafkaListener annotation on spring managed beans:
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value(value = "${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapAddress;

    //The maximum parallelism of a group is that the number of consumers in the group ← no of partitions.
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstants.GROUP_ID_ONE);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        return new DefaultKafkaConsumerFactory<String, String>(props);
    }

    @Bean
    public ConsumerFactory<String, String> consumerPersonInfoFactory() {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstants.GROUP_ID_THREE);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        return new DefaultKafkaConsumerFactory<String, String>(props);
    }

    @Bean
    public ConsumerFactory<String, String> batchConsumerFactory() {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstants.GROUP_ID_TWO);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        return new DefaultKafkaConsumerFactory<String, String>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(10);
        return factory;
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
