package org.suganthan.threadingproblems.blockingQueueWithMutex;

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

    public void enqueue(T item) {
        lock.lock();

        /**
         * Convince yourself that whenever we test the while loop condition size == capacity, we do so while
         * holding the mutex! Also, it may not be immediately obvious, but a different thread can acquire
         * the mutex just when thread releases the mutex and attempts to re-acquire it within the loop.
         */
        while(size == capacity) {
            System.out.println(Thread.currentThread().getName() + " is waiting for enqueue.");
            //Release the mutex to give other threads
            lock.unlock();

            //Reacquire the mutex before checking the conditions
            lock.lock();
        }

        if (tail == capacity) {
            tail = 0;
        }

        array[tail] = item;
        tail++;
        size++;

        System.out.println(Thread.currentThread().getName() + " enqueue " + item);
        lock.unlock();
    }

    public T dequeue() {
        T item;
        lock.lock();

        while (size == 0) {
            System.out.println(Thread.currentThread().getName() + " is waiting for dequeue.");
            lock.unlock();
            lock.lock();
        }

        if (head == capacity) {
            head = 0;
        }

        item = array[head];
        array[head] = null;
        head++;
        size--;

        System.out.println(Thread.currentThread().getName() + " dequeue " + item);
        lock.unlock();
        return item;
    }
}
