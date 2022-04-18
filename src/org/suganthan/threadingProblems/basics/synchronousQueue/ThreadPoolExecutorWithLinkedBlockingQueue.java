package org.suganthan.threadingProblems.basics.synchronousQueue;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorWithLinkedBlockingQueue {
    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 5, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        int i = 0;
        try {
            for (; i < 20; i++) {
                executor.execute(() -> {
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
            executor.shutdown();

            // wait for the executor to shutdown
            executor.awaitTermination(1, TimeUnit.HOURS);
        }
    }
}
