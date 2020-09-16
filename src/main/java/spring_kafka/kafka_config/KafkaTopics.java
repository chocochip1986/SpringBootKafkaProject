package spring_kafka.kafka_config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import spring_kafka.constants.KafkaConstants;

@Configuration
public class KafkaTopics {
    @Bean
    public NewTopic topic1() {
        return new NewTopic(KafkaConstants.TOPIC_ONE, 1, (short)1);
    }
}
