package simple.kafka.backoff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SimpleKafkaBackOffApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(SimpleKafkaBackOffApplication.class, args);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }
}
