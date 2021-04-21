package multi.thread.consumption.functions;

import java.util.Random;
import java.util.function.Supplier;

public class SimpleSupplierCreator {
    public static Supplier<String> createTask(long workerThreadId) {
        return () -> {
            int maxCount = (new Random()).nextInt(10);
            int count = 0;
            System.out.println("["+Thread.currentThread().getId()+"] Slave thread started from Worker thread["+workerThreadId+"] with maxCount = "+maxCount);
            while(count < maxCount) {
                System.out.println("["+Thread.currentThread().getId()+"] Slave thread running...");
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    System.out.println("["+Thread.currentThread().getId()+"] Slave thread interrupted...");
                    Thread.currentThread().interrupt();
                }
                count++;
            }
            System.out.println("["+Thread.currentThread().getId()+"] Slave thread ended...");
            return "EXITED SUCCESSFULLY";
        };
    }
}
