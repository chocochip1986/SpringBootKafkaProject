package egress.example.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EgressFileKafkaApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(EgressFileKafkaApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
