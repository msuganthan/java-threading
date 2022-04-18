package org.suganthan.threadingProblems.basics.synchronousQueue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorWithNewSingleThread {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        int i = 0;
        try {
            for (; i < 20; i++) {
                executorService.execute(() -> {
                    try {
                        System.out.println("Thread " + Thread.currentThread().getName() + " at work.");
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        //ignore for now
                    }
                });
            }
        } catch (RejectedExecutionException ree) {
            System.out.println("Task " + (i + 1) + " rejected.");
        } finally {
            // don't forget to shutdown the executor
            executorService.shutdown();

            // wait for the executor to shutdown
            executorService.awaitTermination(1, TimeUnit.HOURS);
        }
    }
}
