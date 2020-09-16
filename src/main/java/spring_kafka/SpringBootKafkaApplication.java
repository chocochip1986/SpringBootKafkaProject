package spring_kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootKafkaApplication {
    public static void main(String[] args) {
        System.out.println("System thread: "+Thread.currentThread().getId());
        SpringApplication.run(SpringBootKafkaApplication.class, args);
    }
}
