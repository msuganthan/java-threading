package org.suganthan.barberShop;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A barbershop consists of a waiting room with n chairs, and a barber chair for giving haircuts.
 *  1. If there is no customers to be served, the barber goes to sleep.
 *  2. If a customer enter the barbershop and all the chairs are occupied, the the customer leaves the shop.
 *  3. If the barber is busy, but chairs are available, then the customer sits in one of the free chairs.
 *  4. If the barber is asleep, the customer wakes up the barber.
 *
 *  Write a program to co-ordinate the interaction between the barber and the customers.
 *
 *  Solution:
 *
 *  *. A customer enters the shop and if all N chairs are occupied, he leaves. This hints at maintaining a count of the waiting customers.
 *  *. If any of the N chairs are free, the customer takes up the chair to wait for his turn. Note this translates to
 *      using a semaphore on which threads that have found a free chair wait on before being called in by the barber
 *      for a haircut.
 *
 *  *. If a customer enter the shop and the barber is asleep it implies there are no customers in the shop. The just-entered
 *     customer thread wakes up the barber thread. This sounds like using a signaling construct to wake up the barber thread.
 *
 *  We'll have a class which will expose two APIs one for the barber thread to execute and the other for customers.
 */
public class Demonstration {
    public static void main(String[] args) throws InterruptedException {
        BarberShopProblem.runTest();
    }
}

class BarberShopProblem {
    final int CHAIRS                  = 3;
    int waitingCustomers              = 0;
    int hairCutsGiven                 = 0;
    Semaphore waitForCustomerToEnter  = new Semaphore(0);
    Semaphore waitForBarberToGetReady = new Semaphore(0);
    Semaphore waitForCustomerToLeave  = new Semaphore(0);
    Semaphore waitForBarberToCutHair  = new Semaphore(0);
    ReentrantLock lock                = new ReentrantLock();
    /**
     * Customer thread enters the shop, acquires a lock to test the value of the counter {@see waitingForCustomers }.
     * We must test the value of the variable while no other thread can modify its value, hinting that we'll wrap the test
     * under a lock.
     *
     * If the value equals all the chairs available, then the customer thread gives up the lock and returns
     * from the method.
     *
     * If a chair is available the customer thread increments the variable {@see waitingForCustomers }
     *
     * Remember, the barber might be asleep which can be modeled as the barber thread waiting on a semaphore
     * {@see waitForCustomerToEnter}. The customer thread must signal the semaphore {@see waitForCustomerToEnter} in case
     * the barber is asleep.
     *
     * Next, the customer thread itself needs to wait on a semaphore before the barber comes over, greets the customer
     * and leads him to the salon chair. Let's call this semaphore  {@see waitForBarberToGetReady}.
     * {@see waitForBarberToGetReady} is the semaphore the barber signals as soon as it wakes up. All customer threads
     * waiting for a haircut will block on this {@see waitForBarberToGetReady} semaphore.
     * The barber signaling this semaphore is akin to letting one customer come through and sit on the barber chair
     * for a haircut.
     */
    void customerWalksIn() throws InterruptedException {
        lock.lock();
        if (waitingCustomers == CHAIRS) {
            System.out.println("Customer walks out, all chairs occupied.");
            //Remember to unlock before leaving
            lock.unlock();
            return;
        }
        waitingCustomers++;
        lock.unlock();

        //let the barber know you are here, in case he's asleep.
        waitForCustomerToEnter.release();

        //Wait for the barber to come take you to the salon chair when its your turn
        waitForBarberToGetReady.acquire();

        waitForBarberToCutHair.acquire();
        waitForCustomerToLeave.release();

        lock.lock();
        waitingCustomers--;
        lock.unlock();
    }

    /**
     * The barber initially waits on the semaphore {@see waitForCustomerToEnter} to simulate no customer in the shop.
     * If woken up, then it implies that there's at least on customer in the shop who need a hair-cut and the barber
     * gets up, greets the customer and leads him to his chair before starting the haircut. This sequence is translated
     * into code as the barber thread signaling the {@see waitForBarberToGetReady} semaphore. Next, the barber simulates
     * a haircut by sleeping for 50 milliseconds.
     *
     * Once the haircut is done. The barber needs to inform the customer thread too; it does so by signaling the
     * {@see waitForBarberToCutHair} semaphore. The customer thread should already be waiting on this semaphore.
     *
     * Finally, to make the barber thread know that the current customer thread has left the barber chair and the
     * barber can bring in the next customer, we make the barber thread wait on the yet another semaphore
     * {@see waitForCustomerToLeave}. This is the same semaphore the customer thread needs to signal before exiting.
     *
     * @throws InterruptedException
     */
    void barber() throws InterruptedException {
        while (true) {
            //wait till a customer enters a shop
            waitForCustomerToEnter.acquire();

            //let the customer know barber is ready
            waitForBarberToGetReady.release();

            hairCutsGiven++;
            System.out.println("Barber cutting hair..." + hairCutsGiven);
            Thread.sleep(50);

            //let the customer thread know, haircut is done
            waitForBarberToCutHair.release();

            //wait for customer to leave the barber chair
            waitForCustomerToLeave.acquire();
        }
    }

    static void runTest() throws InterruptedException {
        Set<Thread> set = new HashSet<>();
        final BarberShopProblem barberShopProblem = new BarberShopProblem();

        Thread barberThread = new Thread(() -> {
            try {
                barberShopProblem.barber();
            } catch (InterruptedException ie) {
                //Do nothing
            }
        });
        barberThread.start();

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(() -> {
                try {
                    barberShopProblem.customerWalksIn();
                } catch (InterruptedException ie) {
                    //Do nothing
                }
            });
            set.add(t);
        }

        for (Thread t: set)
            t.start();

        for (Thread t: set)
            t.join();

        set.clear();
        Thread.sleep(800);

        for (int i = 0; i < 5; i++) {
            Thread t = new Thread(() -> {
                try {
                    barberShopProblem.customerWalksIn();
                } catch (InterruptedException ie) {
                    //Do Nothing
                }
            });
            set.add(t);
        }

        for (Thread t: set)
            t.start();

        barberThread.join();
    }
}
