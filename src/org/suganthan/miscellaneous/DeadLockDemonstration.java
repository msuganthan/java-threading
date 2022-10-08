package org.suganthan.miscellaneous;

public class DeadLockDemonstration {
    public static void main(String[] args) {
        Deadlock deadlock = new Deadlock();
        try {
            deadlock.runTest();
        } catch (InterruptedException ie) {
        }
    }
}

class Deadlock {
    private int counter = 0;
    private Object lock1 = new Object();
    private Object lock2 = new Object();

    Runnable incrementer = new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                try {
                    incrementCounter();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Incrementing "+i);
            }
        }
    };

    Runnable decrementer = new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                try {
                    decrementCounter();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Incrementing "+i);
            }
        }
    };

    public void runTest() throws InterruptedException {
        Thread thread1 = new Thread(incrementer);
        Thread thread2 = new Thread(decrementer);

        thread1.start();
        //sleep to make sure thread 1 gets chance to acquire lock1
        Thread.sleep(100);

        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("Done :"+counter);
    }

    void incrementCounter() throws InterruptedException {
        synchronized (lock1) {
            System.out.println("Acquired lock1");
            Thread.sleep(100);

            synchronized (lock2) {
                counter++;
            }
        }
    }

    void decrementCounter() throws InterruptedException {
        synchronized (lock2) {
            System.out.println("Acquired lock2");
            Thread.sleep(100);

            synchronized (lock1) {
                counter--;
            }
        }
    }
 }
