package org.suganthan.threadingproblems.blockingQueue;

public class BlockingQueueImplementation {

    public static void main(String[] args) throws InterruptedException {
        final BlockingQueue<Integer> queue = new BlockingQueue<>(5);

        var t1 = new Thread(() -> {
           try {
               for (int i = 0; i < 50; i++) {
                   queue.enqueue(i);
               }
           } catch (InterruptedException e) {
               System.err.println(e);
           }
        });

        var t2 = new Thread(() -> {
            try {
                for (int i = 0; i < 25; i++) {
                    queue.dequeue();
                }
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        });

        var t3 = new Thread(() -> {
            try {
                for (int i = 0; i < 25; i++) {
                    queue.dequeue();
                }
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        });

        t1.start();
        t2.start();
        t2.join();

        t3.start();
        t1.join();
        t3.join();
    }
}
