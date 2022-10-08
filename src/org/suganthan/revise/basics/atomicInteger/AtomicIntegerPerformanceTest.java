package org.suganthan.revise.basics.atomicInteger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerPerformanceTest {
    static AtomicInteger counter = new AtomicInteger(0);
    static int simpleCounter = 0;
    public static void main(String[] args) throws InterruptedException {
        test(true);
        test(false);
    }

    synchronized static void incrementSimpleCounter() {
        simpleCounter++;
    }

    static void test(boolean isAtomic) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        long start = System.currentTimeMillis();

        try {
            for (int i = 0; i < 10; i++) {
                executorService.submit(() -> {
                    for (int j = 0; j < 1000000; j++) {
                        if (isAtomic) {
                            counter.incrementAndGet();
                        } else {
                            incrementSimpleCounter();
                        }
                    }
                });
            }
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);
        }
        long timeTaken = System.currentTimeMillis() - start;
        System.out.println("Time taken by " + (isAtomic ? "atomic integer counter " : "integer counter ") + timeTaken + " milliseconds.");
    }
}
