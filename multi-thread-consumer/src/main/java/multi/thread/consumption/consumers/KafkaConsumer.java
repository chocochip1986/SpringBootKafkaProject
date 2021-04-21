package multi.thread.consumption.consumers;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "topic.one", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        System.out.println("["+Thread.currentThread().getId()+"]Message consumed: "+record.value());
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> {
            //WORKER PROCESSING THREAD
            int maxCount = (new Random()).nextInt(10);
            int count = 0;
            System.out.println("["+Thread.currentThread().getId()+"] Worker thread started with maxCount = "+maxCount);
            while(count < maxCount) {
                System.out.println("["+Thread.currentThread().getId()+"] Worker thread running...");
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    System.out.println("["+Thread.currentThread().getId()+"] Worker thread interrupted...");
                    Thread.currentThread().interrupt();
                }
                count++;
            }
            System.out.println("["+Thread.currentThread().getId()+"] Worker thread ended...");
            acknowledgment.acknowledge();
        });
    }
}
