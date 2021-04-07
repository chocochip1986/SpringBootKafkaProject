package spring_kafka.kafka_config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import spring_kafka.constants.KafkaConstants;

@Configuration
public class KafkaTopics {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public NewTopic topic1() {
        return TopicBuilder
                .name(KafkaConstants.TOPIC_ONE)
                .partitions(KafkaConstants.PARTITION_TEN)
                .build();
    }

    @Bean
    public NewTopic retryTopic() {
        return TopicBuilder.name("retry.topic").partitions(KafkaConstants.PARTITION_TEN).build();
    }

    @Bean
    public NewTopic topic2() {
        return TopicBuilder
                .name(KafkaConstants.TOPIC_TWO)
                .partitions(KafkaConstants.PARTITION_FIVE)
                .build();
    }

    @Bean
    public NewTopic topic3() {
        return TopicBuilder
                .name(KafkaConstants.TOPIC_THREE)
                .partitions(KafkaConstants.PARTITION_TWO)
                .build();
    }

    @Bean NewTopic topic() {
        return TopicBuilder
                .name("datasource-person-create")
                .partitions(1)
                .build();
    }
}
