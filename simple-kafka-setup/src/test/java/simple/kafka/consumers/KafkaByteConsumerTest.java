package simple.kafka.consumers;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import simple.kafka.producers.KafkaByteProducer;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;

@EmbeddedKafka(topics = "test.topic", partitions = 1, brokerProperties = {"spring.kafka.consumer.auto-offset-reset=earliest"})
@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = { "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.auto-offset-reset=earliest"})
public class KafkaByteConsumerTest {
//    @Autowired private KafkaByteConsumer kafkaByteConsumer;
    private final String TOPIC = "topic.two.test";
    @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;
    @Autowired private KafkaByteProducer kafkaByteProducer;

    BlockingQueue<ConsumerRecord<String, byte[]>> topic;
    KafkaMessageListenerContainer<String, byte[]> container;

    @BeforeEach
    public void setUp() {
//        embeddedKafkaBroker = new EmbeddedKafkaBroker(1, false, 1, TOPIC);
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group.two", "false", embeddedKafkaBroker));
        DefaultKafkaConsumerFactory<String, byte[]> consumerFactory = new DefaultKafkaConsumerFactory<>(configs, new StringDeserializer(), new ByteArrayDeserializer());
        ContainerProperties containerProperties = new ContainerProperties(TOPIC);
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        topic = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, byte[]>) topic::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @Test
    public void test1() throws InterruptedException, ExecutionException {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        Producer<String, byte[]> producer = new DefaultKafkaProducerFactory<>(configs, new StringSerializer(), new ByteArraySerializer()).createProducer();

        producer.send(new ProducerRecord<>(TOPIC, "my-aggregate-id", "my-test-value".getBytes(StandardCharsets.UTF_8))).get();
        producer.flush();

        Map<String, Object> consumerConfigs = new HashMap<>(KafkaTestUtils.consumerProps("group.two", "false", embeddedKafkaBroker));
        consumerConfigs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerConfigs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfigs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        consumerConfigs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        org.apache.kafka.clients.consumer.KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<String, byte[]>(consumerConfigs);
//        consumer.subscribe(Collections.singletonList(TOPIC));

        consumer.assign(Collections.singletonList(new TopicPartition(TOPIC, 0)));

//        ConsumerRecord<String, byte[]> record = (ConsumerRecord<String, byte[]>) KafkaTestUtils.getOneRecord(embeddedKafkaBroker.getBrokersAsString(), "group.two", TOPIC, 1, false, false, 10L);
        ConsumerRecords<String, byte[]> records = consumer.poll(100);

//        ConsumerRecord<String, byte[]> singleRecord = topic.poll(100, TimeUnit.MILLISECONDS);
//        assertThat(records.isEmpty()).isFalse();
    }

    @AfterEach
    public void tearDown() {
//        container.stop();
    }

}