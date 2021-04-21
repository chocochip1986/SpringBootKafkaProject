package multi.thread.consumption.consumers;

import multi.thread.consumption.functions.SimpleSupplierCreator;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "topic.one", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        System.out.println("["+Thread.currentThread().getId()+"]Message consumed: "+record.value());
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> {
            ExecutorService slaveThreadService = Executors.newFixedThreadPool(6);
            List<CompletableFuture<String>> listOfFutures = new ArrayList<>();
            for( int i = 0 ; i < 5 ; i++ ) {
                listOfFutures.add(CompletableFuture.supplyAsync(SimpleSupplierCreator.createTask(Thread.currentThread().getId()), slaveThreadService));
            }
            CompletableFuture<Void> futures = CompletableFuture.allOf(listOfFutures.toArray(CompletableFuture[]::new));
            try {
                futures.get();
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("["+Thread.currentThread().getId()+"] Worker thread interrupted...");
                Thread.currentThread().interrupt();
            }
            boolean allPass = listOfFutures.stream().allMatch(CompletableFuture::isDone);
            if (allPass) {
                slaveThreadService.shutdown();
                acknowledgment.acknowledge();
                System.out.println("["+Thread.currentThread().getId()+"] All slave threads exited successfully...");
            } else {
                System.out.println("["+Thread.currentThread().getId()+"] All slave threads exited cui...");
            }
            System.out.println("["+Thread.currentThread().getId()+"] Worker thread ended...");
        });
    }
}
