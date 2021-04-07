package sasl.plaintext.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SaslPlaintextKafkaApplication {
    public static void main(String[] args) {
        System.setProperty("java.security.auth.login.config", System.getProperty("user.dir")+"/sasl-plaintext/src/main/resources/kafka_jaas.conf");
        SpringApplication.run(SaslPlaintextKafkaApplication.class, args);
    }
}
