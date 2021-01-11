package spring_kafka.kafka_config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    //We need to add the KafkaAdmin Spring bean, which will automatically add topics for all beans of type NewTopic
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<String, Object>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configs.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-512");
        configs.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        configs.put("ssl.truststore.location", "/Users/guozheng/Desktop/guozheng/SpringBootKafkaProject/scripts/docker/client/client.truststore.jks");
        configs.put("ssl.truststore.password", "password1234");
        configs.put("ssl.keystore.location", "/Users/guozheng/Desktop/guozheng/SpringBootKafkaProject/scripts/docker/client/client.keystore.jks");
        configs.put("ssl.keystore.password", "password1234");
        configs.put("ssl.key.password", "password1234");
        configs.put("ssl.endpoint.identification.algorithm", "");
        return new KafkaAdmin(configs);
    }
}
