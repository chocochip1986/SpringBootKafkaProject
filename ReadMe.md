# THIS IS AN EXAMPLE OF USING SPRING BOOT WITH KAFKA INTEGRATION

## PREREQUISTE
=====================
You need to install kafka
Goto the scripts/docker/ subdirectory and run the following command:
```
docker-compose -f docker-compose.yml up -d
```

## KAFKA WITH SECURITY
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
  You can find the rest of the classes under org/apache/kafka/common/security/ subdirectory in the spring-kafka dependency.
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

2. SASL\SCRAM-SHA-256 OR SASL\SCRAM-SHA-512
- Run the sasl-scram-512-docker-compose.yml or sasl-scram-512-docker-compose.yml file
- In your KafkaAdmin bean, you need to do implement set the 
  (1) "sasl.mechanism" property. There are a couple of support values (https://www.iana.org/assignments/sasl-mechanisms/sasl-mechanisms.xhtml)
  (2) "security.protocol" property. (PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL)
  similar to this:
```
@Bean
public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<String, Object>();
    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    configs.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-512");
    // OR configs.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-256");
    configs.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
    return new KafkaAdmin(configs);
}
```
- You will also need to set ""sasl.mechanism" and "security.protocol" for ALL consumer and producer factory beans
- You will need to define a kafka_jaas.conf file. If you are using SASL\PLAIN, then you use the corresponding PlainLoginModule class.
  You can find the rest of the classes under org/apache/kafka/common/security/ subdirectory in the spring-kafka dependency.
  Note that the username and password listed here MUST BE THE SAME AS the ones defined in the kafka_jaas.conf file in the Kafka Broker. Look at the docker-compose file for clues.
  The kafka_jaas.conf file can be found in /opt/bitnami/kafka/config/ subdirectory in the bitnami's kafka docker container. It may be in a different location if you're using a different kafka image.
  Save this file some where in your project.
```
KafkaClient {
  org.apache.kafka.common.security.scram.ScramLoginModule required
  username="alice"
  password="alice-secret";
};
```
- The kafka_jaas.conf file which you have created earlier needs to be fed to the Kafka Client's jvm as a property. 
  For example, if your kafka client is a Spring Boot Application, you need it to be set as a property into your Spring Boot Application's JVM.:
```
-Djava.security.auth.login.config="/Users/guozheng/Desktop/guozheng/SpringBootKafkaProject/src/main/resources/kafka_jaas.conf"
```

4. SASL\SCRAM with SSL
- The SCRAM configurations are the same as above. I will only note the SSL configurations here

## KAFKA WITH ACL
====================
1. You will need to set a couple of things within the docker compose file. In the environments section of the kafka docker setup, add these:
```
KAFKA_CFG_AUTHORIZER_CLASS_NAME: "kafka.security.auth.SimpleAclAuthorizer"
KAFKA_CFG_SUPER_USERS: "User:admin"
```
The reason for setting `admin` to be a super user is because `admin` used as the inter-broker username. If you don't set this, the kafka broker will not be able to update metadata as it itself does not have the correct authorizations.
If you have more than 1 super user you can add to the list using the semicolon as a delimiter. Do note that the username specified here is case sensitive.
Note that zookeeper.set.acl is defaulted to false so if you haven't added it, it's ok not to any way. Zookeeper.set.acl is used if you need to modify the Zookeeper nodes(znode) but if we're just setting ACLs on which Kafka Client can produce or consume stuff that it's fine not to set.
```
KAFKA_CFG_ZOOKEEPER_SET_ACL: "false"
```
2. Next, in order for you to create topics FROM the a kafka client, you need to specify a .conf file with similar properties as the following depending on the authentication types you're using.
For example, if you are SASL with SSL, your conf file may look like this. As if you can see below, the `username` and `password` set in the `sasl.jaas.config` property must be the super user.
I think the reason for this is because, by default, with ACL enabled, no resource will be available for anyone unless specified, explicitly, otherwise. Hence you're gonna need a super user to create the topics from an external source.
```
security.protocol=SASL_SSL
sasl.mechanism=SCRAM-SHA-512
ssl.truststore.location=<Absolute Path to your client truststore>/client.truststore.jks
ssl.truststore.password=<Truststore Password>
ssl.keystore.location=<Absolute Path to your client keystore>/client.keystore.jks
ssl.keystore.password=<KeyStore Password>
ssl.key.password=<SSL Password>
ssl.endpoint.identification.algorithm=<intentionally kept blank>
sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username="admin" password="admin-secret";
```
Once this file is setup, you can then run the Kafka CLI tool `kafka-acls` to set some ACLs.
I have not found any other way to set the ACLs.
Example of allowing the user `alice` to create, describe, and read topics
```
/usr/local/bin/kafka-acls --bootstrap-server localhost:9092 --command-config ./src/main/resources/admin.conf --add --allow-principal User:alice --operation Create --operation Describe --operation Read --topic test
```
Next, if you need to verify your ACLs, you should be able to list the ACLs for the given topic
```
/usr/local/bin/kafka-acls --bootstrap-server localhost:9092 --command-config ./src/main/resources/admin.conf --list --topic test
```