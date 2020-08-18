package org.suganthan.miscellaneous;

public class NonReentrantLockDemonstration {
    public static void main(String[] args) throws InterruptedException {
        NonReentrantLock nonReentrantLock = new NonReentrantLock();

        //first locking would be successful.
        nonReentrantLock.lock();
        System.out.println("Acquired first lock");

        // Second locking results in a self deadlock
        System.out.println("Trying to acquire second lock");
        nonReentrantLock.lock();
        System.out.println("Acquired second lock");
    }
}

class NonReentrantLock {
    boolean isLocked;

    public NonReentrantLock() {
        isLocked = false;
    }

    public synchronized void lock() throws InterruptedException {
        while (isLocked) {
            wait();
        }
        isLocked = true;
    }

    public synchronized void unlock() {
        isLocked = false;
        notify();
    }
}
