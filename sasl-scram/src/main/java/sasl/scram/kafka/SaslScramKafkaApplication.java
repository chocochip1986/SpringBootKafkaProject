package sasl.scram.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SaslScramKafkaApplication {
    public static void main(String[] args) {
        System.setProperty("java.security.auth.login.config", System.getProperty("user.dir")+"/sasl-scram/src/main/resources/kafka_jaas.conf");
        SpringApplication.run(SaslScramKafkaApplication.class, args);
    }
}
