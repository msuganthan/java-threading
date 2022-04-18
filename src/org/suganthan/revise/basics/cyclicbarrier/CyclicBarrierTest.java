package org.suganthan.revise.basics.cyclicbarrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierTest {
    public static void main(String[] args) {
        final CyclicBarrier cb = new CyclicBarrier(3, new Runnable() {
            @Override
            public void run() {
                System.out.println("All parties have arrived at the barrier, lets continue execution.");
            }
        });
        Thread t1 = new Thread(new Task(cb), "Thread - 1");
        Thread t2 = new Thread(new Task(cb), "Thread - 2");
        Thread t3 = new Thread(new Task(cb), "Thread - 3");
        t1.start();
        t2.start();
        t3.start();
    }
}

class Task implements Runnable {
    private CyclicBarrier barrier;
    public Task(CyclicBarrier barrier) {
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            System.out.println(Thread.currentThread().getName() + " is waiting on barrier");
            barrier.await();
            System.out.println(Thread.currentThread().getName() + " has crossed the barrier");
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (BrokenBarrierException be) {
            be.printStackTrace();
        }
    }
}
