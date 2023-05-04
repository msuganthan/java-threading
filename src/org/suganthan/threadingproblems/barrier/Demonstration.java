package org.suganthan.threadingproblems.barrier;

/**
 * A barrier can be thought of as a point in the program code, which all or some of the threads need to reach at before
 * any one of them is allowed to proceed further.
 */

/**
 * A barrier allows multiple threads to congregate at a point in code before any one of the threads is allowed to move
 * forward. Java and most other languages provide libraries which make barrier construct available for developer use.
 */

/**
 * We can immediately realize that our solution will need a count variable to track the number of threads that have
 * arrived at the barrier. If we have n threads, then n-1 threads must wait for the nth thread to arrive. This suggests
 * we have the n-1 threads execute the wait method and the nth thread wakes up all the asleep n-1 threads.
 */
public class Demonstration {
    public static void main(String[] args) throws InterruptedException {
        //Barrier.runTestFirstCut();
        Barrier.runTestThirdCut();
    }
}

class Barrier {
    int releasedThread = 0;
    int count = 0;
    int totalThreads;

    public Barrier(int totalThreads) {
        this.totalThreads = totalThreads;
    }

    public synchronized void awaitFirstCut() throws InterruptedException {
        //increment the counter whenever a thread arrives at the barrier.
        count++;
        if (count == totalThreads) {
            //wake up all the threads
            notifyAll();
            //remember to reset count so that barrier can be re-used.
            //This is done so that we are able to re-use the barrier.
            count = 0;
        } else {
            //Wait if you aren't the n'th thread.
            //while (count < totalThreads)
            wait();
        }
    }

    public synchronized void awaitThirdCut() throws InterruptedException {
        //block any new threads from proceeding till, all threads from previous barrier are released.
        while(count == totalThreads) wait();

        //increment the counter whenever a thread arrives at the barrier.
        count++;

        if (count == totalThreads) {
            //wake up all the threads
            notifyAll();

            //remember to set released to totalThreads
            releasedThread = totalThreads;
        } else {
            //wait till the all thread to totalThreads
            while (count < totalThreads)
                wait();
        }

        releasedThread--;

        if (releasedThread == 0) {
            count = 0;
            //remember to wakeup any threads
            notifyAll();
        }
    }

    public static void runTestThirdCut() throws InterruptedException {
        final Barrier barrier = new Barrier(3);

        Thread p1 = new Thread(() -> {
            try {
                System.out.println("Thread 1");
                barrier.awaitThirdCut();
                System.out.println("Thread 1");
                barrier.awaitThirdCut();
                System.out.println("Thread 1");
                barrier.awaitThirdCut();
            } catch (InterruptedException ie) {
            }
        });

        Thread p2 = new Thread(() -> {
            try {
                Thread.sleep(500);
                System.out.println("Thread 2");
                barrier.awaitThirdCut();
                Thread.sleep(500);
                System.out.println("Thread 2");
                barrier.awaitThirdCut();
                Thread.sleep(500);
                System.out.println("Thread 2");
                barrier.awaitThirdCut();
            } catch (InterruptedException ie) {
            }
        });

        Thread p3 = new Thread(() -> {
            try {
                Thread.sleep(1500);
                System.out.println("Thread 3");
                barrier.awaitThirdCut();
                Thread.sleep(1500);
                System.out.println("Thread 3");
                barrier.awaitThirdCut();
                Thread.sleep(1500);
                System.out.println("Thread 3");
                barrier.awaitThirdCut();
            } catch (InterruptedException ie) {
            }
        });

        p1.start();
        p2.start();
        p3.start();

        p1.join();
        p2.join();
        p3.join();
    }

    static void runTestFirstCut() throws InterruptedException {
        final Barrier barrier = new Barrier(3);

        Thread p1 = new Thread(() -> {
            try {
                System.out.println("Thread 1");
                barrier.awaitFirstCut();
                System.out.println("Thread 1");
                barrier.awaitFirstCut();
                System.out.println("Thread 1");
                barrier.awaitFirstCut();
            } catch (InterruptedException ie) {
            }
        });

        Thread p2 = new Thread(() -> {
           try {
               Thread.sleep(500);
               System.out.println("Thread 2");
               barrier.awaitFirstCut();
               Thread.sleep(500);
               System.out.println("Thread 2");
               barrier.awaitFirstCut();
               Thread.sleep(500);
               System.out.println("Thread 2");
               barrier.awaitFirstCut();
           } catch (InterruptedException ie) {
           }
        });

        Thread p3 = new Thread(() -> {
            try {
                Thread.sleep(1500);
                System.out.println("Thread 3");
                barrier.awaitFirstCut();
                Thread.sleep(1500);
                System.out.println("Thread 3");
                barrier.awaitFirstCut();
                Thread.sleep(1500);
                System.out.println("Thread 3");
                barrier.awaitFirstCut();
            } catch (InterruptedException ie) {
            }
        });

        p1.start();
        p2.start();
        p3.start();

        p1.join();
        p2.join();
        p3.join();
    }
}
