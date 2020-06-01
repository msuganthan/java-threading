package org.suganthan.blockingQueueWithMutex;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueueWithMutex<T> {
    T[] array;
    Lock lock = new ReentrantLock();
    int size = 0;
    int capacity;
    int head = 0;
    int tail = 0;

    public BlockingQueueWithMutex(int capacity) {
        // The casting results in a warning
        array = (T[]) new Object[capacity];
        this.capacity = capacity;
    }

    public void enqueue(T item) throws InterruptedException {
        lock.lock();

        while(size == capacity) {
            //Release the mutex to give other threads
            lock.unlock();

            //Reacquire the mutex before checking the conditions
            lock.lock();
        }

        if (tail == capacity)
            tail = 0;

        array[tail] = item;
        tail++;
        size++;

        lock.unlock();
    }

    public T dequeue() throws InterruptedException {
        T item;
        lock.lock();

        while (size == 0) {
            lock.unlock();
            lock.lock();
        }

        if (head == capacity)
            head = 0;

        item = array[head];
        array[head] = null;
        head++;
        size--;

        lock.unlock();
        return item;
    }


}
