package simple.kafka.retry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SimpleKafkaRetryApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(SimpleKafkaRetryApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
