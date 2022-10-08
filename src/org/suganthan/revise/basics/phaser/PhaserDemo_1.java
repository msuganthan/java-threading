package org.suganthan.revise.basics.phaser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

public class PhaserDemo_1 {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        //create an instance of Phaser class and register only a single that will arrive at the barrier

        Phaser phaser = new Phaser(1);
        try {
            //a thread registers with the Phaser post construction of the instance.
            executorService.submit(() -> {
                phaser.register();
            });

            //main thread bulk-registers two more parties
            phaser.bulkRegister(2);

            //main thread registering one more party
            phaser.register();

            //We now have 5 parties registered with the Phaser instance
            //We instantiate four threads and have them arrive at the barrier
            for (int i = 0; i < 4; i++) {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        phaser.arriveAndAwaitAdvance();
                        System.out.println(Thread.currentThread().getName() + " moving past barrier. ");
                    }
                });
            }

            Thread.sleep(2000);
            // before arriving at the barrier, print the counts of parties
            System.out.println(Thread.currentThread().getName() + " just before arrived. \n Arrived parties: " + phaser.getArrivedParties() +
                    "\n Registered parties: " + phaser.getRegisteredParties() +
                    "\n Unarrived parties: " + phaser.getUnarrivedParties());

            phaser.arriveAndAwaitAdvance();
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);
        }
    }
}
