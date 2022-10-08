package org.suganthan.revise.basics.lock;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockExample {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newFixedThreadPool(5);
        ReentrantLock lock = new ReentrantLock();

        Runnable threadA = () -> threadA(lock);
        Runnable threadB = () -> threadB(lock);

        /**
         * Here we are having thread threads, the main thread, threadA, and threadB interacting with an
         * instance of {@link ReentrantLock}. The main thread locks the lock object thrice and invokes
         * `unlock` on the object with an artificially introduced delay. During this time, `threadA` does a
         * busy spinning waiting for the lock to be free. `threadA` uses the method `tryLock()` to check if
         * it can acquire the lock. On the other hand `threadB` simply acquires and then releases the lock.
         * `threadB` will inevitably block at acquiring the lock and the method `getQueueLength()` will
         * display a count of 1. The example also demonstrates the use of the method `getHoldCount()` which
         * either returns 0 of the lock isn't help by the thread invoking the method or the number of times
         * the owning thread has recursively acquired the lock.
         *
         * Finally, there's the `isLocked` method that returns true if the lock is held by any thread.
         * However, not that both methods `isLocked` and `getQueueLength` are designed for monitoring the
         * state of the system and shouldn't be used in program control. e.g. you should never do something
         * like below:
         *
         * ```
         * if(lock.isLocked()) {
         *     //take some action.
         * }
         * ```
         */
        try {
            lock.lock();
            lock.lock();
            lock.lock();

            System.out.println("Main thread lock hold count = " + lock.getHoldCount());

            Future future1 = es.submit(threadA);
            Future future2 = es.submit(threadB);

            for (int i = 0; i < 3; i++) {
                Thread.sleep(50);
                lock.unlock();
            }

            System.out.println("Main thread released lock. Lock hold count = " + lock.getHoldCount());
            future1.get();
            future2.get();
        } finally {
            for (int i = 0; i < lock.getHoldCount(); i++) {
                lock.unlock();
            }
            es.shutdown();
        }
    }

    static void threadB(Lock lock) {
        lock.lock();
        lock.unlock();
    }

    static void threadA(ReentrantLock lock) {
        String name = "THREAD-A";
        Thread.currentThread().setName(name);
        boolean keepTrying = true;

        System.out.println("Is lock owned by any other thread = "+lock.isLocked());

        while (keepTrying) {
            System.out.println(name + " trying to acquire lock");
            if (lock.tryLock()) {
                try {
                    System.out.println(name + " acquired lock");
                    keepTrying = false;
                } finally {
                    lock.unlock();
                    System.out.println(name + " release lock");
                }
            } else {
                System.out.println(name + " failed to acquire lock. Other threads waiting = "+lock.getQueueLength());
            }

            try {
                Thread.sleep(20);
            } catch (InterruptedException ie) {}
        }
    }
}
