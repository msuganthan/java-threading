package org.suganthan.threadingproblems.basics.threadpoolexecutor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorTest {
    public static void main(String[] args) {
        ThreadPoolExecutor executor =
                new ThreadPoolExecutor(1,
                        5,
                        1,
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<>(3),
                        new ThreadPoolExecutor.AbortPolicy());

        try {
            for (int i = 0; i < 6; i++) {
                executor.submit(() -> {
                    System.out.println("This is worker thread "+Thread.currentThread().getName() + " executing");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {

                    }
                });
            }
        } finally {
            executor.shutdown();
        }
    }
}
