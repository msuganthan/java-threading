package org.suganthan.miscellaneous.missedSignals;

import java.util.concurrent.Semaphore;

public class MissedSignalFix {
    public static void main(String[] args) throws InterruptedException {
        FixesMissedSignal.example();
    }
}

class FixesMissedSignal {
    public static void example() throws InterruptedException {
        final Semaphore semaphore = new Semaphore(1);

        Thread signaler = new Thread(new Runnable() {
            @Override
            public void run() {
                semaphore.release();
                System.out.println("Send signal");
            }
        });


        Thread waiter = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                System.out.println("Received Signal..");
            }
        });

        signaler.start();
        signaler.join();

        Thread.sleep(5000);
        waiter.start();
        waiter.join();

        System.out.println("Program exiting");
    }
}
