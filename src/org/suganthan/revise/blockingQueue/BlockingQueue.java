package org.suganthan.revise.blockingQueue;

public class BlockingQueue<T> {
    T[] array;
    int head;
    int tail;
    int size;
    int capacity;
    Object lock = new Object();

    public BlockingQueue(int capacity) {
        array = (T[]) new Object[capacity];
        this.capacity = capacity;
    }

    public void enqueue(T item) throws InterruptedException {
        synchronized (lock) {
            if (size == capacity)
                lock.wait();

            if (tail == capacity)
                tail = 0;

            array[tail] = item;
            tail++;
            size++;

            lock.notifyAll();
        }
    }

    public T deque() throws InterruptedException {
        T item;
        synchronized (lock) {
            if (size == 0)
                lock.wait();

            if (head == capacity)
                head = 0;

            item = array[head];
            head++;
            size--;

            lock.notifyAll();
        }
        return item;
    }

    public static void main(String[] args) throws InterruptedException {
        final BlockingQueue<Integer> q = new BlockingQueue<>(5);

        Thread t1 = new Thread(() -> {
            try {
                for (int i = 0; i < 50; i++) {
                    q.enqueue(i);
                    System.out.println("enqueued " + i);
                }
            } catch (InterruptedException ignored) {

            }
        });

        Thread t2 = new Thread(() -> {
            try {
                for (int i = 0; i < 25; i++) {
                    System.out.println("Thread 2 dequeued: " + q.deque());
                }
            } catch (InterruptedException ignored) {

            }
        });

        Thread t3 = new Thread(() -> {
            try {
                for (int i = 0; i < 25; i++) {
                    System.out.println("Thread 3 dequeued: " + q.deque());
                }
            } catch (InterruptedException ignored) {

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
