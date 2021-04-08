package sasl.scram.ssl.kafka.configs;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfigs {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaTemplate<String, byte[]> kafkaByteTemplate() {
        return new KafkaTemplate<>(producerByteFactory());
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        String projectRootDir = System.getProperty("user.dir");
        Map<String, Object> configProps = new HashMap<String, Object>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-512");
        configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        configProps.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, projectRootDir+"/scripts/docker/client/client.truststore.jks");
        configProps.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "password1234");
        configProps.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, projectRootDir+"/scripts/docker/client/client.keystore.jks");
        configProps.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "password1234");
        configProps.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "password1234");
        configProps.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
        return new DefaultKafkaProducerFactory<String, String>(configProps);
    }

    @Bean
    public ProducerFactory<String, byte[]> producerByteFactory() {
        String projectRootDir = System.getProperty("user.dir");
        Map<String, Object> configProps = new HashMap<String, Object>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        configProps.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-512");
        configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        configProps.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, projectRootDir+"/scripts/docker/client/client.truststore.jks");
        configProps.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "password1234");
        configProps.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, projectRootDir+"/scripts/docker/client/client.keystore.jks");
        configProps.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "password1234");
        configProps.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "password1234");
        configProps.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
        return new DefaultKafkaProducerFactory<>(configProps);
    }
}
