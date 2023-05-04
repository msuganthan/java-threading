package org.suganthan.threadingproblems.blockingQueueUsingSemaphore;

/**
 * 1. Java's semaphore can be released even if none of the permits, the Java semaphore was initialized with, have been used.
 * 2. Java's semaphore has no upper bound and can be released as many times as desired to increase the number of permits.
 */
public class CountingSemaphore {
    int usedPermits = 0;
    int maxCount;

    public CountingSemaphore(int maxCount, int initialPermits) {
        this.maxCount = maxCount;
        this.usedPermits = this.maxCount - initialPermits;
    }

    /**
     * If the usedPermits equals the maxCount, the {@link CountingSemaphore} will have to wait to acquire a new lock.
     */
    public synchronized void acquire() throws InterruptedException {
        while(usedPermits == maxCount)
            wait();

        notify();
        usedPermits++;
    }

    /**
     * If the usedPermits is nothing, then there is nothing it release, so the {@link CountingSemaphore} has to wait to release the lock.
     */
    public synchronized void release() throws InterruptedException {
        while (usedPermits == 0)
            wait();

        usedPermits--;
        notify();
    }
}
