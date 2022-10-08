package org.suganthan.revise.countingSemaphore;

public class CountingSemaphore {
    int usedPermits = 0;
    int maxCount;

    public CountingSemaphore(int maxCount) {
        this.maxCount = maxCount;
    }

    public synchronized void acquire() throws InterruptedException {
        while (usedPermits == maxCount)
            wait();

        notify();
        usedPermits++;
    }

    public synchronized void release() throws InterruptedException {
        while (usedPermits == 0)
            wait();

        usedPermits--;
        notify();
    }
}
