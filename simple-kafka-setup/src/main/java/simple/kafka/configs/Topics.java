package simple.kafka.configs;

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
}
