package org.suganthan.revise.barbershop;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class BarberShopProblem {
    final int CHAIRS = 3;
    /**
     * The customer thread must signal the semaphore waitForCustomerToEnter in case the barber is asleep.
     */
    Semaphore waitForCustomerToEnter = new Semaphore(0);

    /**
     * Customer itself needs to wait on a semaphore before the barber comes over, greets the customer and
     * leads him to the salon chair.
     *
     * This is the same semaphore the barber signals as soon as it wakes up. All customer thread waiting for
     * a haircut will block in this waitForBarberToGetReady semaphore.
     *
     * The barber signaling this semaphore is akin a letting one customer come through and sit on the barber
     * chair for a haircut.
     */
    Semaphore waitForBarberToGetReady = new Semaphore(0);
    Semaphore waitForCustomerToLeave = new Semaphore(0);
    Semaphore waitForBarberToCutHair = new Semaphore(0);

    int waitingCustomers = 0;
    ReentrantLock lock = new ReentrantLock();
    int hairCutsGiven = 0;

    void customerWalksIn() throws InterruptedException {
        lock.lock();
        if (waitingCustomers == CHAIRS) {
            System.out.println("Customer walks out, all chairs occupied");
            lock.unlock();
            return;
        }
        waitingCustomers++;
        lock.unlock();

        //Let the barber know you are here, in case he's asleep
        waitForCustomerToEnter.release();
        //Wait for the ba
        waitForBarberToGetReady.acquire();

        //Wait for haircut to complete.
        waitForBarberToCutHair.acquire();

        //Leave the barber chair and let barber thread k now chair is vacant.
        waitForCustomerToLeave.release();

        lock.lock();
        waitingCustomers--;
        lock.unlock();
    }

    void barber() throws InterruptedException {
        while (true) {
            waitForCustomerToEnter.acquire();

            /**
             * There's atleast one customer in the shop who need a hair-cut and the barber gets up, greets the customer and leads him to his chair before starting the haircut.
             */
            waitForBarberToGetReady.release();
            hairCutsGiven++;
            System.out.println("Barber cutting hair..." + hairCutsGiven);
            Thread.sleep(50);
            /**
             * The barber needs to inform the customer thread too; The customer thread should already be
             * waiting on this semaphore.
             */
            waitForBarberToCutHair.release();

            /**
             * To make the barber thread know that the current customer thread has left the barber chair and
             * the barber can bring in the next customer, we make barber thread wait on yet another semaphore
             * waitForCustomerToLeave. This is the same semaphore the customer thread needs to signal before
             * exiting.
             */
            waitForCustomerToLeave.acquire();
        }
    }
}
