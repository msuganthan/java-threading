package org.suganthan.threadingProblems.deferredCallback;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Design and implement a thread-safe class that allows registration of callback method that are executed after a user
 * specified time interval in seconds has elapsed.
 */
public class Demonstration {
    public static void main(String[] args) throws InterruptedException {
        DeferredCallbackExecutor.runTestTenCallbacks();
    }
}

class DeferredCallbackExecutor {
    private static final Random random = new Random(System.currentTimeMillis());

    /**
     * The earliest callback sit on top of the queue.
     */
    PriorityQueue<Callback> callbackPriorityQueue = new PriorityQueue<>((o1, o2) -> (int) (o1.executeAt - o2.executeAt));

    //Lock to guard critical section.
    Lock lock = new ReentrantLock();

    //The execution thread will wait on it while the consumer threads will signal it. The condition variable allows the
    //consumer threads to wake up the execution thread whenever a new callback is registered.
    Condition newCallbackArrived = lock.newCondition();

    public static void runTestTenCallbacks() throws InterruptedException {
        Set<Thread> allThreads = new HashSet<>();
        final DeferredCallbackExecutor executor = new DeferredCallbackExecutor();

        Thread service = new Thread(() -> {
            try {
                executor.start();
            } catch (InterruptedException ie) {
            }
        });
        service.start();

        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                Callback callback = new Callback(1, "Hello this is " + Thread.currentThread().getName());
                executor.registerCallback(callback);
            });
            thread.setName("Thread_ " + (i + 1));
            thread.start();
            allThreads.add(thread);
            Thread.sleep(1000);
        }

        for (Thread t : allThreads) {
            t.join();
        }
    }

    private long findSleepDuration() {
        long currentTime = System.currentTimeMillis();
        return callbackPriorityQueue.peek().executeAt - currentTime;
    }

    /**
     * This method will add a new callback to our min heap.  In java the generic {@link PriorityQueue} is an
     * implementation of a heap which can be passed a comparator to either act as a min or max heap. In case
     * we pass in a comparator in the constructor so that the callbacks are ordered by their execution times,
     * the earliest callback to be executed sits at the top of the heap.
     */
    public void start() throws InterruptedException {
        long sleepFor = 0;

        while (true) {
            lock.lock();

            /**
             * Initially the queue will be empty and the execution thread should just wait indefinitely on the condition variable to be signaled.
             */
            while (callbackPriorityQueue.size() == 0)
                newCallbackArrived.await();
            /**
             * When the first callback gets registered, we note how many seconds after its arrival does it need to be executed
             * and {@code await} on the condition variable for that many seconds.
             */
            while (callbackPriorityQueue.size() != 0) {
                sleepFor = findSleepDuration();

                if (sleepFor <= 0)
                    break;

                newCallbackArrived.await(sleepFor, TimeUnit.MILLISECONDS);
            }

            // Because we have a min-heap the first element of the queue
            // is necessarily the one which is due.
            Callback cb = callbackPriorityQueue.poll();
            System.out.println("Executed at " + System.currentTimeMillis() / 1000 + " required at " + cb.executeAt / 1000 + " :message: " + cb.message);

            lock.unlock();
        }
    }

    /**
     * I don't understand this point:
     * Waiting on the condition variable, will not be able to make progress until the consumer thread gives up the lock,
     * even though the condition has been signaled.
     *
     * @param callback
     */
    public void registerCallback(Callback callback) {
        lock.lock();
        callbackPriorityQueue.add(callback);
        newCallbackArrived.signal();
        lock.unlock();
    }

    static class Callback {
        long executeAt;
        String message;

        public Callback(long executeAfter, String message) {
            this.executeAt = System.currentTimeMillis() + (executeAfter * 1000);
            this.message = message;
        }
    }
}