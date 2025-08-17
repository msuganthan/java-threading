package org.suganthan.threadingproblems.blockingQueue;

public class BlockingQueue<T> {

    Object lock = new Object();
    private T[] array;
    private int capacity;
    private int size = 0;
    private int head;
    private int tail;

    BlockingQueue(int capacity) {
        array = (T[]) new Object[capacity];
        this.capacity = capacity;
    }

    public void enqueue(T item) throws InterruptedException {
        synchronized (lock) {
            while (size == capacity) {
                System.out.println(Thread.currentThread().getName() + " is waiting for enqueue");
                lock.wait();
            }

            if (tail == capacity) {
                tail = 0;
            }

            array[tail] = item;
            size++;
            tail++;

            System.out.println(Thread.currentThread().getName()+" enqueue "+item);
            lock.notifyAll();
        }
    }

    public T dequeue() throws InterruptedException {
        T item;
        synchronized (lock) {
            while (size == 0) {
                System.out.println(Thread.currentThread().getName() + " is waiting for dequeue");
                lock.wait();
            }

            if (head == capacity) {
                head = 0;
            }

            item = array[head];
            array[head] = null;
            head++;
            size--;

            System.out.println(Thread.currentThread().getName()+" dequeue "+item);
            lock.notifyAll();
        }
        return item;
    }
}
