package org.suganthan.revise.basics.phaser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

public class PhaserDemo_3 {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(15);

        Phaser phaser = new Phaser(2);

        try {
            int arrivalPhase = phaser.register();
            for (int i = 0; i < 2; i++) {
                executorService.submit(() -> {
                    for (int i1 = 0; i1 < 15; i1++) {
                        phaser.arriveAndAwaitAdvance();

                        if (i1 > 10) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ie) {}
                        }
                    }
                    System.out.println(Thread.currentThread().getName() + " proceeding forward.");
                });
            }

            while (arrivalPhase < 10) {
                arrivalPhase = phaser.arriveAndAwaitAdvance();
                System.out.println("Main thread arrived at phase "+arrivalPhase);
            }
            phaser.arriveAndDeregister();
            System.out.println("main thread past the barrier");
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);
        }
    }
}
