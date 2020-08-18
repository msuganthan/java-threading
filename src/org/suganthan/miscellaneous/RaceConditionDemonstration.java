package org.suganthan.miscellaneous;

import java.util.Random;

public class RaceConditionDemonstration {
    public static void main(String[] args) throws InterruptedException {
        RaceCondition.runTest();
    }
}

class RaceCondition {
    int randInt;
    Random random = new Random(System.currentTimeMillis());

    void printer() {
        int i = 1000000;
        while (i != 0) {
            if (randInt % 5 == 0)
                if (randInt % 5 != 0)
                    System.out.println(randInt);
            i--;
        }
    }


    void modifier() {
        int i = 1000000;
        while(i != 0) {
            randInt = random.nextInt(1000);
            i--;
        }
    }

    public static void runTest() throws InterruptedException {
        final RaceCondition raceCondition = new RaceCondition();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                raceCondition.printer();
            }
        });

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                raceCondition.modifier();
            }
        });

        thread.start();
        thread1.start();

        thread.join();
        thread1.join();
    }
}