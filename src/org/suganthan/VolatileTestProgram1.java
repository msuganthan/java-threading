package org.suganthan;

/**
 * Reference: https://www.youtube.com/watch?v=SC2jXxOPe5E
 *
 * This programs explain, how long it will take to count upto 100_000_000
 */
public class VolatileTestProgram1 {
    private volatile int a;
    private volatile int b;
    /*private int a;
    private int b;*/

    private static long lastA;
    private static long lastB;

    public static void main(String[] args) {
        final VolatileTestProgram1 instance = new VolatileTestProgram1();
        new Thread(() -> {
            lastA = System.nanoTime();
            while (true) {
                instance.a++;
                if (instance.a % 100_000_000 == 0) {
                    System.out.println("A: " +(System.nanoTime() - lastA) / 1000000 + " ms");
                    lastA = System.nanoTime();
                    instance.a=0;
                }
            }
        }).start();

        new Thread(() -> {
            lastB = System.nanoTime();
            while (true) {
                instance.b++;
                if (instance.b % 100_000_000 == 0) {
                    System.out.println("B: " +(System.nanoTime() - lastB) / 1000000 + " ms");
                    lastB = System.nanoTime();
                    instance.b=0;
                }
            }
        }).start();
    }
}
