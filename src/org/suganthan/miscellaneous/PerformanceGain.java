package org.suganthan.miscellaneous;

public class PerformanceGain {
    public static void main(String[] args) throws InterruptedException {
        SumUpExample.oneThread();
        SumUpExample.twoThread();
    }
}

class SumUpExample {
    long start;
    long end;
    long counter = 0;
    static long MAX_NUM = Integer.MAX_VALUE;

    SumUpExample(long start, long end) {
        this.start = start;
        this.end   = end;
    }

    public void add() {
        for (long i = this.start; i <= this.end; i++) {
            this.counter += i;
        }
    }

    static public void twoThread() throws InterruptedException {
        long start = System.currentTimeMillis();
        SumUpExample sumUpExample = new SumUpExample(1, MAX_NUM/2);
        SumUpExample sumUpExample1 = new SumUpExample(1 + (MAX_NUM/2), MAX_NUM);

        Thread t1 = new Thread(() -> sumUpExample.add());

        Thread t2 = new Thread(() -> sumUpExample1.add());

        t1.start();
        t1.join();

        t2.start();
        t2.join();

        long finalCount = sumUpExample.counter + sumUpExample1.counter;
        long end = System.currentTimeMillis();
        System.out.println("Two threads final count = " + finalCount + " took " + (end - start));

    }
    
    static public void oneThread() {
        long start = System.currentTimeMillis();
        SumUpExample sumUpExample = new SumUpExample(1, MAX_NUM);
        sumUpExample.add();
        long end = System.currentTimeMillis();
        System.out.println("Single thread final count = " + sumUpExample.counter + " took " + (end - start));
    }
}