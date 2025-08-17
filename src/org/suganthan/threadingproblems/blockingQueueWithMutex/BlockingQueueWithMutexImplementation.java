package org.suganthan.threadingproblems.blockingQueueWithMutex;

public class BlockingQueueWithMutexImplementation {
    public static void main(String[] args) throws InterruptedException {
        final BlockingQueueWithMutex<Integer> q = new BlockingQueueWithMutex<>(5);

        var producer1 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                q.enqueue(i);
            }
        });

        var producer2 = new Thread(() -> {
            for (int i = 100; i < 150; i++) {
                q.enqueue(i);
            }
        });

        var producer3 = new Thread(() -> {
            for (int i = 200; i < 250; i++) {
                q.enqueue(i);
            }
        });

        var consumer1 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                q.dequeue();
            }
        });

        var consumer2 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                q.dequeue();
            }
        });

        var consumer3 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                q.dequeue();
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
