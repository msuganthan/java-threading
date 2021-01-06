package org.suganthan.threadingProblems.blockingQueue;

public class BlockingQueue<T> {
    Object lock = new Object();
    private T[] array;
    private int capacity; //==> determine the size
    private int size = 0; //==> Moving pointer
    private int head; //==> pointer for dequeue
    private int tail; //==> pointer for enqueue

    BlockingQueue(int capacity) {
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
            size++;
            tail++;
            lock.notifyAll();
        }
    }

    public T dequeue() throws InterruptedException {
        T item;
        synchronized (lock) {
            if (size == 0)
                lock.wait();

            if (head == capacity)
                head = 0;

            item = array[head];
            array[head] = null;
            head++;
            size--;

            lock.notifyAll();
        }
        return item;
    }
}
