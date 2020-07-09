package org.suganthan.miscellaneous.raceCondition;

import java.util.Random;

public class IncorrectRaceConditionExample {
    public static void main(String[] args) throws InterruptedException {
        IncorrectRaceCondition.runTest();
    }
}

class IncorrectRaceCondition {
    int randInt;

    Random random = new Random(System.currentTimeMillis());

    void printer() {
        int i = 1000000;
        while (i != 0) {
            /**
             * The printer thread checks if the shared variable is divisible by 5 but before the thread can
             * print the variable out, it value changed by the modifier thread. Some of the printed values aren't
             * divisible by 5 which verifies the existence of a race condition in the code.
             */
            if (randInt % 5 == 0) {
                if (randInt % 5 != 0)
                    System.out.println(randInt);
            }
            i--;
        }
    }

    void modifier() {
        int i = 1000000;
        while (i != 0) {
            randInt = random.nextInt(1000);
            i--;
        }
    }

    public static void runTest() throws InterruptedException {
        final IncorrectRaceCondition incorrectRaceCondition = new IncorrectRaceCondition();
        Thread thread1 = new Thread(() -> incorrectRaceCondition.printer());

        Thread thread2 = new Thread(() -> incorrectRaceCondition.modifier());

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }
}
