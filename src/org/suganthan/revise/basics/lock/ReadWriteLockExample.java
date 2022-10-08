package org.suganthan.revise.basics.lock;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockExample {
    /**
     * In the simple case we can relax the condition that reader are okay to read stale data from the cache.
     * We can imagine that a single writer thread periodically writer to the cache and readers don't mind if
     * the data get stale before the next update by the writer thread. In this scenario, the only caution to
     * exercise is to make sure no readers are reading the cache when a writer is in the process of writing
     * to the cache.
     */
    static Random random = new Random();
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(15);

        Map<String, Object> cache = new HashMap<>();
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        cache.put("key", -1);

        Runnable writerTask = () -> writerThread(cache, lock);
        Runnable readerTask = () -> readerThread(cache, lock);

        try {
            Future future1 = es.submit(writerTask);
            Future future2 = es.submit(readerTask);
            Future future3 = es.submit(readerTask);
            Future future4 = es.submit(readerTask);
            Future future5 = es.submit(readerTask);

            future1.get();
            future2.get();
            future3.get();
            future4.get();
            future5.get();
        } finally {
            es.shutdown();
        }
    }

    static void writerThread(Map<String, Object> cache, ReadWriteLock lock) {
        for (int i = 0; i < 9; i++) {
            try {
                Thread.sleep(random.nextInt(50));
            } catch (InterruptedException ie) {}

            lock.writeLock().lock();
            try {
                System.out.println("Acquired write lock");
                cache.put("key", random.nextInt(1000));
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    static void readerThread(Map<String, Object> cache, ReadWriteLock lock) {
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException ie) {}
            lock.readLock().lock();
            try {
                System.out.println("Acquire read lock and reading key = "+cache.get("key"));
            } finally {
                lock.readLock().unlock();
            }
        }
    }
}
