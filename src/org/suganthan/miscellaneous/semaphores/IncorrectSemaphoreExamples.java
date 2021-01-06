package org.suganthan.miscellaneous.semaphores;

import java.util.concurrent.Semaphore;

public class IncorrectSemaphoreExamples {
    public static void main(String[] args) throws InterruptedException {
        IncorrectSemaphore.example();
    }
}

class IncorrectSemaphore {
    public static void example() throws InterruptedException {
        final Semaphore semaphore = new Semaphore(1);

        Thread badThread = new Thread(() -> {
            while (true) {
                try {
                    System.out.println("Bad thread started");
                    semaphore.acquire();
                } catch (InterruptedException interruptedException) {
                }
                throw new RuntimeException("Exception happens at runtime");
            }
        });
        badThread.start();
        Thread.sleep(1000);
        final Thread goodThread = new Thread(() -> {
            System.out.println("Good thread patiently waiting to be signaled");
            try {
                semaphore.acquire();
            } catch (InterruptedException interruptedException) {}
        });

        goodThread.start();
        badThread.join();
        goodThread.join();
        System.out.println("Exiting Program...");
    }
}
