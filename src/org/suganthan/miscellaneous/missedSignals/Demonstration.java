package org.suganthan.miscellaneous.missedSignals;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Demonstration {
    public static void main(String[] args) throws InterruptedException {
        MissedSignal.example();
    }
}

class MissedSignal {
    public static void example() throws InterruptedException {
        final ReentrantLock lock = new ReentrantLock();
        final Condition condition = lock.newCondition();

        Thread signaller = new Thread(() -> {
            lock.lock();
            condition.signal();
            System.out.println("Send Signal");
            lock.unlock();
        });

        Thread waiter = new Thread(() -> {
            lock.lock();
            try {
                //The idiomatic way of using await is in a while loop with an associated boolean condition
                condition.await();
                System.out.println("Received signal");
            } catch (InterruptedException interruptedException) {

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
