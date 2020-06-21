package org.suganthan.uberRiding;

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
            //Seat all the democrats in the Uber ride.
            demsWaiting.release();
            democrats -= 4;
            rideLeader = true;
        } else if (democrats == 2 && republicans >= 2) {
            //Seat 2 democrats & 2 republicans
            demsWaiting.release(1);
            repubsWaiting.release(2);
            rideLeader = true;
            democrats -= 2;
            republicans -= 2;
        } else {
            lock.unlock();
            demsWaiting.acquire();
        }

        seated();
        barrier.await();

        if (rideLeader == true) {
            drive();
            lock.unlock();
        }
    }

    void seatRepublican() throws InterruptedException, BrokenBarrierException {

    }

    void seated() {

    }

    void drive() {
        System.out.println("Uber Ride on Its wayyyy... with ride leader " + Thread.currentThread().getName());
        System.out.flush();
    }
}
