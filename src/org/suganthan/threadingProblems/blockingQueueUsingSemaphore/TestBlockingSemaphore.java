package org.suganthan.threadingProblems.blockingQueueUsingSemaphore;

public class TestBlockingSemaphore {

    public static void main(String[] args) throws InterruptedException {

        final BlockingQueueWithSemaphore<Integer> queue = new BlockingQueueWithSemaphore<>(5);

        Thread t1 = new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    queue.enqueue(new Integer(i));
                    System.out.println("Enqueue "+i);
                }
            } catch (InterruptedException ie) {

            }
        });

        Thread t2 = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++)
                    System.out.println("Thread 2 dequeued: "+ queue.dequeue());
            } catch (InterruptedException ie) {

            }
        });

        Thread t3 = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++)
                    System.out.println("Thread 3 dequeued: " + queue.dequeue());
            } catch (InterruptedException ie) {

            }
        });

        t1.start();
        Thread.sleep(4000);
        t2.start();
        t2.join();

        t3.start();
        t1.join();
        t3.join();

    }
}
