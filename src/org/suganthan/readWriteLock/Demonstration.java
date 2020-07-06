package org.suganthan.readWriteLock;

public class Demonstration {
    public static void main(String[] args) throws InterruptedException {

        final ReadWriteLock readWriteLock = new ReadWriteLock();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Attempting to acquire write lock in t1: "+ System.currentTimeMillis());
                    readWriteLock.acquireWriteLock();
                    System.out.println("Write lock acquired t1: "+ System.currentTimeMillis());

                    //Simulate write lock being held indefinitely
                    for (;;)
                        Thread.sleep(500);
                } catch (InterruptedException interruptedException) {

                }
            }
        });

        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    System.out.println("Attempting to acquire write lock in t2: " + System.currentTimeMillis());
                    readWriteLock.acquireWriteLock();
                    System.out.println("write lock acquired t2: " + System.currentTimeMillis());
                } catch (InterruptedException ie) {
                }
            }
        });

        Thread tReader1 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    readWriteLock.acquireReadLock();
                    System.out.println("Read lock acquired: " + System.currentTimeMillis());
                } catch (InterruptedException ie) {
                }
            }
        });

        Thread tReader2 = new Thread(new Runnable() {

            @Override
            public void run() {
                System.out.println("Read lock about to release: " + System.currentTimeMillis());
                readWriteLock.releaseReadLock();
                System.out.println("Read lock released: " + System.currentTimeMillis());
            }
        });

        tReader1.start();
        t1.start();

        Thread.sleep(3000);
        tReader2.start();
        Thread.sleep(1000);
        t2.start();
        tReader1.join();
        tReader2.join();
        t2.join();

    }
}

class ReadWriteLock {
    boolean isWriteLocked = false;
    int readers = 0;

    public synchronized void acquireReadLock() throws InterruptedException {
        while (isWriteLocked)
            wait();

        readers++;
    }

    public synchronized  void releaseReadLock() {
        readers--;
        notify();
    }

    public synchronized void acquireWriteLock() throws InterruptedException {
        while (isWriteLocked || readers != 0)
            wait();

        isWriteLocked = true;
    }

    public synchronized void releaseWriteLock() {
        isWriteLocked = false;
        notify();
    }
}
