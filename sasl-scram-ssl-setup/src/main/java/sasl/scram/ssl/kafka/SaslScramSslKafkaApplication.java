package sasl.scram.ssl.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SaslScramSslKafkaApplication {
    public static void main(String[] args) {
        System.setProperty("java.security.auth.login.config", System.getProperty("user.dir")+"/sasl-scram-ssl-setup/src/main/resources/kafka_jaas.conf");
        SpringApplication.run(SaslScramSslKafkaApplication.class, args);
    }
}
