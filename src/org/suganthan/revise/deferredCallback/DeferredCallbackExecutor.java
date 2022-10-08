package org.suganthan.revise.deferredCallback;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class DeferredCallbackExecutor {

    Queue<Callback> queue = new PriorityQueue<>((o1, o2) -> (int) (o1.executeAt - o2.executeAt));
    ReentrantLock lock = new ReentrantLock();
    Condition newCallbackArrived = lock.newCondition();

    private long findSleepDuration() {
        long currentTime = System.currentTimeMillis();
        return queue.peek().executeAt - currentTime;
    }

    public void start() throws InterruptedException {
        long sleepFor;

        while (true) {
            lock.lock();
            while (queue.size() == 0) {
                newCallbackArrived.await();
            }

            while (queue.size() != 0) {
                sleepFor = findSleepDuration();

                if (sleepFor <= 0)
                    break;

                newCallbackArrived.await(sleepFor, MILLISECONDS);
            }

            Callback callback = queue.poll();
            System.out.println(
                    "Executed at " + System.currentTimeMillis()/1000 + " required at " + callback.executeAt/1000
                            + ": message:" + callback.message);

            lock.unlock();
        }
    }

    void registerCallback(Callback callback) {
        lock.lock();
        queue.add(callback);
        newCallbackArrived.signal();
        lock.unlock();
    }

    static class Callback {
        long executeAt;
        String message;
        public Callback(long executeAt, String message) {
            this.executeAt = executeAt;
            this.message = message;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Set<Thread> threads = new HashSet<>();
        DeferredCallbackExecutor executor = new DeferredCallbackExecutor();
        Thread service = new Thread(() -> {
            try {
                executor.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        service.start();

        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                Callback callback = new Callback(1, "Hello this is " + Thread.currentThread().getName());
                executor.registerCallback(callback);
            });
            thread.setName("Thread_" + (i + 1));
            thread.start();
            threads.add(thread);
            Thread.sleep(1000);
        }
        for (Thread t: threads) {
            t.join();
        }
    }
}
