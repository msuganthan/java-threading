package org.suganthan.threadingproblems.basics.synchronousqueue;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorWithSynchronousQueue {
    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 5, 1, TimeUnit.MINUTES, new SynchronousQueue<>());

        int i = 0;
        try {
            for (; i < 50; i++) {
                executor.execute(() -> {
                    try {
                        System.out.println("Thread " + Thread.currentThread().getName() + " at work.");
                        Thread.sleep(1000);
                    } catch (InterruptedException ignore) {
                        //ignore it
                    }
                });
            }
        } catch (RejectedExecutionException rejected) {
            System.out.println("Task " + (i + 1) + " rejected.");
        } finally {
            // don't forget to shutdown the executor
            executor.shutdown();

            // wait for the executor to shutdown
            executor.awaitTermination(1, TimeUnit.HOURS);
        }
    }
}
