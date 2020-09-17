package spring_kafka.kafka_config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import spring_kafka.constants.KafkaConstants;

@Configuration
public class KafkaTopics {
    @Bean
    public NewTopic topic1() {
        return TopicBuilder
                .name(KafkaConstants.TOPIC_ONE)
                .partitions(10)
                .build();
    }
}
