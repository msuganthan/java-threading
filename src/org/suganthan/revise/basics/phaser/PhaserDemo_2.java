package org.suganthan.revise.basics.phaser;

import java.util.concurrent.Phaser;

public class PhaserDemo_2 {
    public static void main(String[] args) {
        Phaser phaser = new Phaser(1);

        int phase = phaser.getPhase();
        System.out.println("Starting at phase : "+phase);

        //arrive and print the current phase
        phase = phaser.arriveAndAwaitAdvance();
        System.out.println("phase "+phase);

        //arrive and print the current phase
        phase = phaser.arriveAndAwaitAdvance();
        System.out.println("phase "+phase);

        //arrive and print the current phase
        phase = phaser.arriveAndAwaitAdvance();
        System.out.println("phase "+phase);

        //arrive and print the current phase
        phase = phaser.arriveAndAwaitAdvance();
        System.out.println("phase "+phase);
    }
}
