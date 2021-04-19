package simple.kafka.recovery.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class Topics {

    @Bean
    public NewTopic topic1() {
        return TopicBuilder
                .name("topic.one")
                .partitions(1)
                .build();
    }

    @Bean
    public NewTopic topicDlt() {
        return TopicBuilder.name("topic.one.dlt").partitions(1).build();
    }

    @Bean
    public NewTopic topic2() {
        return TopicBuilder
                .name("topic.two")
                .partitions(1)
                .build();
    }

    @Bean
    public NewTopic topic2Dlt() {
        return TopicBuilder
                .name("topic.two.dlt")
                .partitions(1)
                .build();
    }

    @Bean
    public NewTopic topic3() {
        return TopicBuilder
                .name("topic.three")
                .partitions(1)
                .build();
    }
}
