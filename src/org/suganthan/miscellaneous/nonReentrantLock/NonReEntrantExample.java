package org.suganthan.miscellaneous.nonReentrantLock;

public class NonReEntrantExample {
    public static void main(String[] args) throws InterruptedException {
        NonReEntrant nonReEntrant = new NonReEntrant();

        nonReEntrant.lock();
        System.out.println("Acquired first lock...");

        System.out.println("Trying to acquire second lock...");
        nonReEntrant.lock();

        System.out.println("Acquired second lock...");
    }
}

class NonReEntrant {
    boolean isLocked;

    NonReEntrant() {
        isLocked = false;
    }

    public synchronized void lock() throws InterruptedException {
        while (isLocked) {
            wait();
        }
        isLocked = true;
    }

    public synchronized void unLock() throws InterruptedException {
        isLocked = false;
        notify();
    }
}
