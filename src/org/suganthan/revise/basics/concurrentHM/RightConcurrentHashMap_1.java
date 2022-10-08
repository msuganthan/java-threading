package org.suganthan.revise.basics.concurrentHM;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RightConcurrentHashMap_1 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ConcurrentHashMap<String, MyCounter> map = new ConcurrentHashMap<>();
        map.put("Biden", new MyCounter());
        ExecutorService es = Executors.newFixedThreadPool(5);

        Runnable task = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    MyCounter myCounter = map.get("Biden");

                    synchronized (myCounter) {
                        myCounter.increment();
                    }
                }
            }
        };

        Future future1 = es.submit(task);
        Future future2 = es.submit(task);

        future1.get();
        future2.get();

        es.shutdown();

        System.out.println("votes for Biden = " + map.get("Biden").getCount());
    }

    static class MyCounter {
        private int count = 0;

        void increment() {
            count++;
        }

        int getCount() {
            return count;
        }
    }
}
