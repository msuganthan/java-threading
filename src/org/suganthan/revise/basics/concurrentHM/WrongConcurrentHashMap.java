package org.suganthan.revise.basics.concurrentHM;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WrongConcurrentHashMap {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        map.put("Biden", 0);

        ExecutorService es = Executors.newFixedThreadPool(5);
        Runnable task = () -> {
            for (int i = 0; i < 100; i++) {
                /**
                 * The below line is doing three operations
                 * 1. retrieval of the value
                 * 2. incrementing the value
                 * 3. updating the value.
                 *
                 * So this is not a right implementation.
                 *
                 * The right implementation should execute all the three steps together as a transaction to avoid synchronization issues. The takeaway is that a {@link ConcurrentHashMap} doesn't protect its constituents from race conditions but access to the data structure itself its thread safe.
                 */
                map.put("Biden", map.get("Biden") + 1);
            }
        };

        Future future1 = es.submit(task);
        Future future2 = es.submit(task);

        future1.get();
        future2.get();

        es.shutdown();

        System.out.println("Votes for Biden = "+map.get("Biden"));
    }
}
