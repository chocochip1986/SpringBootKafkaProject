package rebalancing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaRebalancingApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(KafkaRebalancingApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
