package org.suganthan.revise.basics.concurrentHM;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class RightConcurrentHashMap {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ConcurrentHashMap<String, AtomicInteger> map = new ConcurrentHashMap<String, AtomicInteger>();
        AtomicInteger ai = new AtomicInteger(0);
        map.put("Biden", ai);

        ExecutorService es = Executors.newFixedThreadPool(5);
        Runnable task = () -> {
            for (int i = 0; i < 100; i++) {
                map.get("Biden").incrementAndGet();
            }
        };

        Future future1 = es.submit(task);
        Future future2 = es.submit(task);

        future1.get();
        future2.get();

        es.shutdown();
        System.out.println("votes for Biden = " + map.get("Biden").get());
    }
}
