package org.suganthan.revise.basics.atomicboolean;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AtomicBooleanTest {
    static volatile  boolean won = false;
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(25);
        try {
            int numThreads = 25;
            Runnable[] racers = new Runnable[numThreads];
            Future[] futures = new Future[numThreads];

            for (int i = 0; i < numThreads; i++) {
                racers[i] = () -> race();
            }

            for (int i = 0; i < numThreads; i++) {
                futures[i] = es.submit(racers[i]);
            }

            for (int i = 0; i < numThreads; i++) {
                futures[i].get();
            }
        } finally {
            es.shutdown();
        }
    }

    static void race() {
        if (!won) {
            won = true;
            System.out.println(Thread.currentThread().getName() + " won the race.");
        } else {
            System.out.println(Thread.currentThread().getName() + " lost.");
        }
    }
}
