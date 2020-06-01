package org.suganthan.blockingQueue;

public class BlockingQueueImplementation {

    public static void main(String[] args) throws InterruptedException {
        final BlockingQueue<Integer> queue = new BlockingQueue<>(5);

        Thread t1 = new Thread(() -> {
           try {
               for (int i = 0; i < 50; i++) {
                   queue.enqueue(new Integer(i));
                   System.out.println("enqueued "+i);
               }
           } catch (InterruptedException e) {

           }
        });

        Thread t2 = new Thread(() -> {
            try {
                for (int i = 0; i < 25; i++) {
                    System.out.println("Thread 2 dequeued: "+ queue.dequeue());
                }
            } catch (InterruptedException ie) {

            }
        });

        Thread t3 = new Thread(() -> {
            try {
                for (int i = 0; i < 25; i++) {
                    System.out.println("Thread 3 dequeued: " + queue.dequeue());
                }
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
