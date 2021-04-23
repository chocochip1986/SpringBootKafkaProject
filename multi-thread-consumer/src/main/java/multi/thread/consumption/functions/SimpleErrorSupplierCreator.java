package multi.thread.consumption.functions;

import java.util.function.Supplier;

public class SimpleErrorSupplierCreator {
    public static Supplier<String> createTask(long workerThreadId) {
        return () -> {
            System.out.println("["+Thread.currentThread().getId()+"] Slave thread started from Worker thread["+workerThreadId+"]");
            try {
                System.out.println("["+Thread.currentThread().getId()+"] Slave thread running...");
                Thread.sleep(5000L);
                throw new RuntimeException("["+Thread.currentThread().getId()+"] Error encounted!");
            } catch (InterruptedException e) {
                System.out.println("["+Thread.currentThread().getId()+"] Slave thread interrupted...");
            }
            System.out.println("["+Thread.currentThread().getId()+"] Slave thread ended...");
            return "EXITED SUCCESSFULLY";
        };
    }
}
