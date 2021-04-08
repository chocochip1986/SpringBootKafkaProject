package sasl.scram.ssl.kafka.configs;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfigs {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        String projectRootDir = System.getProperty("user.dir");
        Map<String, Object> configs = new HashMap<String, Object>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configs.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-512");
        configs.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        configs.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, projectRootDir+"/scripts/docker/client/client.truststore.jks");
        configs.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "password1234");
        configs.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, projectRootDir+"/scripts/docker/client/client.keystore.jks");
        configs.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "password1234");
        configs.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "password1234");
        configs.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
        return new KafkaAdmin(configs);
    }
}
