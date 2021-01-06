THIS IS AN EXAMPLE OF USING SPRING BOOT WITH KAFKA INTEGRATION

PREREQUISTE
=====================
You need to install kafka
Goto the scripts/docker/ subdirectory and run the following command:
```
docker-compose -f docker-compose.yml up -d
```



KAFKA WITH SECURITY
====================

1. SASL\PLAIN
- Run the sasl-plaintext-docker-compose file
- In your KafkaAdmin bean, you need to do implement set the 
  (1) "sasl.mechanism" property. There are a couple of support values (https://www.iana.org/assignments/sasl-mechanisms/sasl-mechanisms.xhtml)
  (2) "security.protocol" property. (PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL)
  similar to this:
```
@Bean
public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<String, Object>();
    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    configs.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
    configs.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
    return new KafkaAdmin(configs);
}
```
- You will also need to set ""sasl.mechanism" and "security.protocol" for ALL consumer and producer factory beans
- You will need to define a kafka_jaas.conf file. If you are using SASL\PLAIN, then you use the corresponding PlainLoginModule class.
  You can find the rest of the classes under org/apache/kafka/cmoon/security/ subdirectory in the spring-kafka dependency.
  Note that the username and password listed here MUST BE THE SAME AS the ones defined in the kafka_jaas.conf file in the Kafka Broker. Look at the docker-compose file for clues.
  The kafka_jaas.conf file can be found in /opt/bitnami/kafka/config/ subdirectory in the bitnami's kafka docker container. It may be in a different location if you're using a different kafka image.
  Save this file some where in your project.
```
KafkaClient {
  org.apache.kafka.common.security.plain.PlainLoginModule required
  username="alice"
  password="alice-secret";
};
```
- The kafka_jaas.conf file which you have created earlier needs to be fed to the Kafka Client's jvm as a property. 
  For example, if your kafka client is a Spring Boot Application, you need it to be set as a property into your Spring Boot Application's JVM.:
```
-Djava.security.auth.login.config="/Users/guozheng/Desktop/guozheng/SpringBootKafkaProject/src/main/resources/kafka_jaas.conf"
```


