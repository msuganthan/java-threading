package org.suganthan.miscellaneous;

import java.util.Random;

public class DemoThreadUnsafe {

    static Random random = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws InterruptedException {

        ThreadUnSafeCounter badCounter = new ThreadUnSafeCounter();

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                badCounter.incrementCounter();
                DemoThreadUnsafe.sleepRandomlyForLessthan10Secs();
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                badCounter.decrementCounter();
                DemoThreadUnsafe.sleepRandomlyForLessthan10Secs();
            }
        });

        //Run both threads
        thread1.start();
        thread2.start();

        //Wait for t1 and t2 to complete.
        thread1.join();
        thread2.join();

        //print the final value of counter
        badCounter.printFinalCounterValue();
    }

    public static void sleepRandomlyForLessthan10Secs() {
        try {
            Thread.sleep(random.nextInt(10));
        } catch (InterruptedException ie) {

        }
    }

}

class ThreadUnSafeCounter {
    int counter = 0;

    public void incrementCounter() {
        counter++;
    }

    public void decrementCounter() {
        counter--;
    }

    public void printFinalCounterValue() {
        System.out.println("Counter is: "+counter);
    }
}
