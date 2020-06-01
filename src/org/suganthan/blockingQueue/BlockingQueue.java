package org.suganthan.blockingQueue;

public class BlockingQueue<T> {
    T[] array;
    Object lock = new Object();
    int size = 0;
    int capacity;
    int head = 0;
    int tail = 0;

    public BlockingQueue(int capacity) {
        array = (T[]) new Object[capacity];
        this.capacity = capacity;
    }

    public void enqueue(T item) throws InterruptedException {
        synchronized (lock) {
            //wait for queue to have space
            while (size == capacity)
                lock.wait();

                //reset tail to the beginning if the tail is already
                //at the end of the backing array
                if (tail == capacity)
                    tail = 0;

                //place the item in the array
                array[tail] = item;
                size++;
                tail++;


                //don't forget to notify any other threads waiting on
                //a change in value of size. There might be consumer's
                //waiting for the queue to have atleast one element.
                lock.notifyAll();
        }

    }

    public T dequeue() throws InterruptedException {
        T item;

        synchronized (lock) {
            //wait for atleast one item to be enqueued
            while(size == 0)
                lock.wait();

            //reset head to start of array if its past the array
            if (head == capacity)
                head = 0;

            //store the reference to the object being dequeued
            //and overwrite with null
            item = array[head];
            array[head] = null;
            head++;
            size--;

            //don't forget to call notify, there might be another thread
            //blocked in the enqueue method.
            lock.notifyAll();
        }

        return item;
    }
}
