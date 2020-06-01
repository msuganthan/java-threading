package org.suganthan.blockingQueue;

public class BlockingQueueImplementation {

    static class BlockingQueue<T> {
        T[] array;
        int size = 0;
        int capacity;
        int head = 0;
        int tail = 0;
    }


}
