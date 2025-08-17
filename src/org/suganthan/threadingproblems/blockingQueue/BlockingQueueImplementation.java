package org.suganthan.threadingproblems.blockingQueue;

public class BlockingQueueImplementation {

    public static void main(String[] args) throws InterruptedException {
        final BlockingQueue<Integer> q = new BlockingQueue<>(5);

        var producer1 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                try {
                    q.enqueue(i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        var producer2 = new Thread(() -> {
            for (int i = 100; i < 150; i++) {
                try {
                    q.enqueue(i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        var producer3 = new Thread(() -> {
            for (int i = 200; i < 250; i++) {
                try {
                    q.enqueue(i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        var consumer1 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                try {
                    q.dequeue();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        var consumer2 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                try {
                    q.dequeue();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        var consumer3 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                try {
                    q.dequeue();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        producer1.start();
        producer2.start();
        producer3.start();

        consumer1.start();
        consumer2.start();
        consumer3.start();

        producer1.join();
        producer2.join();
        producer3.join();

        consumer1.join();
        consumer2.join();
        consumer3.join();
    }
}
