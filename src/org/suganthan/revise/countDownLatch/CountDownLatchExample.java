package org.suganthan.revise.countDownLatch;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchExample {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(2);

        Worker A = new Worker(countDownLatch, "A");
        Worker B = new Worker(countDownLatch, "B");

        A.start();
        B.start();

        countDownLatch.await();

        Master D = new Master("Master executed");
        D.start();
    }
}

class Worker extends Thread {

    private CountDownLatch countDownLatch;

    public Worker(CountDownLatch countDownLatch, String name) {
        super(name);
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        System.out.println("Worker "+Thread.currentThread().getName()+ " started");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Worker "+Thread.currentThread().getName()+ " finished");
        countDownLatch.countDown();
    }
}

class Master extends Thread {
    public Master(String name) {
        super(name);
    }

    @Override
    public void run() {
        System.out.println("Master executed "+Thread.currentThread().getName());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
