package org.suganthan.threadingproblems.semaphore;

/**
 * Java does provide its owm implementation of Semaphore, however, Java's semaphore is initialized with an initial
 * number of permits, rather than the maximum possible permits and the develop is expected to take care of always
 * releasing the intended number of maximum permits.
 *
 * Briefly, a semaphore is a construct that allows some threads to access a fixed set of resources in parallel. Always think
 * of a semaphore as having a fixes number of permits to give out. Once all the permits are given out, requesting threads,
 * needs to wait for a permit to be returned before proceeding forward.
 *
 * Task is to implement a semaphore which takes in its constructor the maximum number of permits allowed and is also
 * initialized with the same number of permits
 */

/**
 * We need a function to `gain the permit` and a function to `return the permit`
 *
 * 1. acquire() function to simulate gaining   a permit.
 * 2. release() function to simulate releasing a permit.
 *
 * The constructor accepts an integer parameter defining the number of permits available with the semaphore.
 * Internally we need to store a count which keeps track of the permits given out so far.
 *
 */
public class Demonstration {

    public static void main(String[] args) throws InterruptedException {
        final CountingSemaphore cs = new CountingSemaphore(1);

        Thread t1 = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    cs.acquire();
                    System.out.println("Ping "+ i);
                }
            } catch (InterruptedException ie) {
                //Do Nothing
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    cs.release();
                } catch (InterruptedException ie) {
                    //Do Nothing
                }
            }
        });

        t2.start();
        t1.start();

        t1.join();
        t2.join();
    }
}


class CountingSemaphore {
    int usedPermits = 0; //permits given out
    int maxCount;  //max permits to give out

    public CountingSemaphore(int maxCount) {
        this.maxCount = maxCount;
    }

    public synchronized void acquire() throws InterruptedException {
        if (usedPermits == maxCount)
            wait();

        usedPermits++;
        notify();
    }

    public synchronized void release() throws InterruptedException {
        if (usedPermits == 0)
            wait();

        usedPermits--;
        notify();
    }
}