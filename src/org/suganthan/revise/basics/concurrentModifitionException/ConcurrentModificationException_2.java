package org.suganthan.revise.basics.concurrentModifitionException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConcurrentModificationException_2 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Map<String, Integer> map = new HashMap<>();
        ExecutorService es = Executors.newFixedThreadPool(5);

        try {
            Runnable reader = () -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {}

                for (Map.Entry<String, Integer> entry: map.entrySet()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        System.out.println("key "+entry.getKey() + " value "+entry.getValue());
                    }
                }
            };

            Runnable writer = () -> {
                for (int i = 0; i < 100; i++) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ie) {}
                    map.put("key-"+i, i);
                }
            };

            Future future1 = es.submit(writer);
            Future future2 = es.submit(reader);

            future1.get();
            future2.get();
        } finally {
            es.shutdown();
        }
    }
}
