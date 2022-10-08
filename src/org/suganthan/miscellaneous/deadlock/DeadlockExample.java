package org.suganthan.miscellaneous.deadlock;

public class DeadlockExample {
    public static void main(String[] args) throws InterruptedException {
        new Deadlock().runTest();
    }
}

class Deadlock {
    int counter = 0;
    Object lock1 = new Object();
    Object lock2 = new Object();

    Runnable incrementer = () -> {
        try {
            for (int i = 0; i < 100; i++) {
                incrementCounter();
                System.out.println("Incrementing " + i);
            }
        } catch (InterruptedException interruptedException) {

        }
    };

    Runnable decrementer = () -> {
        try {
            for (int i = 0; i < 100; i++) {
                decrementCounter();
                System.out.println("Decrementing " + i);
            }
        } catch (InterruptedException interruptedException) {

        }
    };

    void incrementCounter() throws InterruptedException{
        synchronized (lock1) {
            System.out.println("Acquired lock1");
            Thread.sleep(100);
            synchronized (lock2) {
                counter++;
            }
        }
    }

    void decrementCounter() throws InterruptedException{
        synchronized (lock2) {
            System.out.println("Acquired lock2");
            Thread.sleep(100);
            synchronized (lock1) {
                counter--;
            }
        }
    }

    public void runTest() throws InterruptedException {
        Thread thread1 = new Thread(incrementer);
        Thread thread2 = new Thread(decrementer);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
        System.out.println("Done :"+ counter);
    }
}
