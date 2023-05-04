package org.suganthan.threadingproblems.missedSignal;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MissedSignalExample {
    public static void main(String[] args) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        Thread signaller = new Thread(() -> {
            lock.lock();
            condition.signal();
            System.out.println("Signal sent.");
            lock.unlock();
        });

        Thread waiter = new Thread(() -> {
            lock.lock();
            try {
                condition.await();
                System.out.println("Signal received.");
            } catch (InterruptedException e) {

            }
            lock.unlock();
        });

        signaller.start();
        signaller.join();

        waiter.start();
        waiter.join();

        System.out.println("Program exiting...");
    }

}
