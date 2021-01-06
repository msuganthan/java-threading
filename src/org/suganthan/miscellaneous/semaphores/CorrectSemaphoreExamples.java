package org.suganthan.miscellaneous.semaphores;

import java.util.concurrent.Semaphore;

public class CorrectSemaphoreExamples {
    public static void main(String[] args) throws InterruptedException {
        CorrectSemaphore.example();
    }
}

class CorrectSemaphore {
    public static void example() throws InterruptedException {
        final Semaphore semaphore = new Semaphore(1);

        Thread badThread = new Thread(() -> {
            while (true) {
                try {
                    semaphore.acquire();
                    try {
                        throw new RuntimeException("");
                    } catch (Exception e) {
                        // handle any program logic exception and exit the function
                        return;
                    } finally {
                        System.out.println("Bad thread releasing semahore.");
                        semaphore.release();
                    }

                } catch (InterruptedException ie) {
                    // handle thread interruption
                }
            }
        });

        badThread.start();

        Thread.sleep(1000);

        final Thread goodThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Good thread patiently waiting to be signaled");
                try {
                    semaphore.acquire();
                } catch (InterruptedException interruptedException) {

                }
            }
        });

        goodThread.start();
        badThread.join();
        goodThread.join();
        System.out.println("Exiting Program...");
    }
}
