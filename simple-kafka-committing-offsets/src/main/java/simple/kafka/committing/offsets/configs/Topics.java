package simple.kafka.committing.offsets.configs;

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
                .build();
    }

    @Bean
    public NewTopic topic2() {
        return TopicBuilder
                .name("topic.two")
                .build();
    }

    @Bean
    public NewTopic topic3() {
        return TopicBuilder
                .name("topic.three")
                .build();
    }

    @Bean
    public NewTopic topic4() {
        return TopicBuilder
                .name("topic.four")
                .build();
    }

    @Bean
    public NewTopic topic5() {
        return TopicBuilder
                .name("topic.five")
                .build();
    }

    @Bean
    public NewTopic topic6() {
        return TopicBuilder
                .name("topic.six")
                .build();
    }

    @Bean
    public NewTopic topic7() {
        return TopicBuilder
                .name("topic.seven")
                .build();
    }

    @Bean
    public NewTopic topic8() {
        return TopicBuilder
                .name("topic.eight")
                .build();
    }
}
