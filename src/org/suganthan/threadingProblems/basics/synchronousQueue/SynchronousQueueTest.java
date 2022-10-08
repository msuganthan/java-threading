package org.suganthan.threadingProblems.basics.synchronousQueue;

import java.util.concurrent.SynchronousQueue;

public class SynchronousQueueTest {
    public static void main(String[] args) throws InterruptedException {
        SynchronousQueue queue = new SynchronousQueue();
        queue.put(7);
        System.out.println("I am blocked...");
    }
}
