package org.suganthan.threadingproblems.uberRiding;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Imaginary Uber ride problem where Republicans and Democrats can't be seated as a minority in a four passenger car.
 *
 * Your task as the Uber developer is to model the ride requestors as threads. Once an acceptable combination of riders
 * is possible, threads are allowed to proceed to ride.
 *
 * Each thread invokes the method { @see seated() } when selected by the system for the next ride. When all the
 * threads are seated, any one of the four threads can invoke the method drive() to inform the driver to start the ride.
 */
public class Demonstration {
    public static void main( String args[] ) throws InterruptedException {
        UberSeatingProblem.runTest();
    }
}

class UberSeatingProblem {
    private int republicans = 0;
    private int democrats   = 0;

    private Semaphore demsWaiting   = new Semaphore(0);
    private Semaphore repubsWaiting = new Semaphore(0);

    CyclicBarrier barrier = new CyclicBarrier(4);
    ReentrantLock lock    = new ReentrantLock();


    /**
     * For simplicity imagine the first thread is a democrat and invokes { @see seatDemocrat() }. Since there is no
     * other rider available, it should be put to wait. We can use a semaphore to make this thread wait. We'll not use a
     * barrier, because we don't know what party loyalty the threads arriving in future would have. It might be that the
     * next four threads are all republican and this Democrat isn't placed on the next Uber ride. To
     * differentiate between waiting democrats and waiting republicans, we'll use two different semaphores
     * demsWaiting and repubsWaiting. Our first democrat thread will end-up {@see acquire} - ing the
     * demsWaiting semaphore.
     * @throws InterruptedException
     * @throws BrokenBarrierException
     */
    void seatDemocrat() throws InterruptedException, BrokenBarrierException {
        boolean rideLeader = false;
        lock.lock();

        democrats++;

        if (democrats == 4) {
            /**
             * If there are already 3 waiting democrats, then we signal the {@see demsWaiting} three times
             * so that all these four democrats can ride together in the next Uber ride.
             */
            demsWaiting.release(3);
            democrats -= 4;
            rideLeader = true;
        } else if (democrats == 2 && republicans >= 2) {
            /**
             * If there are two or more republican thread waiting and at least two democrat thread waiting,
             * then the current democrat thread can signal the {@see repubsWaiting} semaphore twice to release
             * the two waiting republican thread and signal the {@see demsWaiting} semaphore once to release one more
             * democrats thread. Together the four of them would make up the next ride consisting of two
             * republican and two democrats.
             */
            demsWaiting.release(1);
            repubsWaiting.release(2);
            rideLeader = true;
            democrats -= 2;
            republicans -= 2;
        } else {
            /**
             * If the above two conditions aren't true then the current democrat thread should simply wait itself at the
             * @{see demsWaiting} semaphore and release the lock object so that other threads can enter the critical sections.
             */
            lock.unlock();
            demsWaiting.acquire();
        }

        seated();
        barrier.await();

        if (rideLeader) {
            drive();
            lock.unlock();
        }
    }

    void seatRepublican() throws InterruptedException, BrokenBarrierException {
        boolean rideLeader = false;
        lock.lock();

        republicans++;

        if (republicans == 4) {
            repubsWaiting.release(3);
            rideLeader = true;
            republicans -= 4;
        } else if (republicans == 2 && democrats >= 2) {
            repubsWaiting.release(1);
            demsWaiting.release(2);
            rideLeader = true;
            republicans -= 2;
            democrats -= 2;
        } else {
            lock.unlock();
            repubsWaiting.acquire();
        }

        seated();
        barrier.await();

        if (rideLeader) {
            drive();
            lock.unlock();
        }
    }

    void seated() {
        System.out.println(Thread.currentThread().getName() + "  seated");
        System.out.flush();
    }

    void drive() {
        System.out.println("Uber Ride on Its wayyyy... with ride leader " + Thread.currentThread().getName());
        System.out.flush();
    }

    static void runTest() throws InterruptedException {
        final UberSeatingProblem uberSeatingProblem = new UberSeatingProblem();
        Set<Thread> allThreads = new HashSet<Thread>();

        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        uberSeatingProblem.seatDemocrat();
                    } catch (InterruptedException ie) {
                        System.out.println("We have a problem");
                    } catch (BrokenBarrierException bbe) {
                        System.out.println("We have a problem");
                    }
                }
            });
            thread.setName("Democrat_"+(i + 1));
            allThreads.add(thread);
            Thread.sleep(50);
        }

        for (int i = 0; i < 14; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        uberSeatingProblem.seatRepublican();
                    } catch (InterruptedException ie) {
                        System.out.println("We have a problem");
                    } catch (BrokenBarrierException bbe) {
                        System.out.println("We have a problem");
                    }
                }
            });
            thread.setName("Republican_"+(i + 1));
            allThreads.add(thread);
            Thread.sleep(20);
        }

        for (Thread t: allThreads)
            t.start();

        for (Thread t: allThreads)
            t.join();
    }
}
