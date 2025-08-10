package org.suganthan.miscellaneous;

import static java.lang.Integer.MAX_VALUE;

public class PerformanceGain {
    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        add(0, MAX_VALUE);
        System.out.println("Time taken " + (System.currentTimeMillis() - startTime));

        long startTime1 = System.currentTimeMillis();
        int half = MAX_VALUE / 2;
        Thread t1 = new Thread(() -> add(0, half));
        Thread t2 = new Thread(() -> add(half, MAX_VALUE));
        t1.start();
        t2.start();

        t1.join();
        t2.join();
        System.out.println("Time taken " + (System.currentTimeMillis() - startTime1));
    }

    public static long add(int start, int end) {
        long sum = 0;
        for (int i = start; i < end; i++) {
            sum += i;
        }
        return sum;
    }
}