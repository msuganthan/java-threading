package org.suganthan.threadingproblems.diningPhilosophers;

import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * Imagine you have fix philosopher's sitting on a roundtable. The philosopher's do only two kinds of activities. One they
 * contemplate, and two they eat. However, they have only five forks between themselves to eat their food with. Each
 * philosophers requires both the fork to his left and fork to his right to eat his food.
 */

/**
 * For no deadlock to occur at all and have all the philosopher be able to eat, we would need ten forks, two for each
 * philosophers. With five forks available, at most, only two philosophers will be able to eat while letting a third
 * hungry philosopher to hold onto the fifth fork and wait for another one to become available before he can eat.
 */

/**
 * Think of each fork as a resource that needs to be owned by one of the philosophers sitting on either side.
 */
public class Demonstration {
    public static void main(String args[]) throws InterruptedException {
        DiningPhilosophers.runTest();
    }

}

class DiningPhilosophers {
    private static Random random = new Random(System.currentTimeMillis());

    //Five semaphore represent the five forks.
    //Each philosopher can then be thought of as a thread that tries to acquire the fork to the left and right of it.
    private Semaphore[] forks = new Semaphore[5];
    private Semaphore maxDiners = new Semaphore(4);

    public DiningPhilosophers() {
        forks[0] = new Semaphore(1);
        forks[1] = new Semaphore(1);
        forks[2] = new Semaphore(1);
        forks[3] = new Semaphore(1);
        forks[4] = new Semaphore(1);
    }

    //Represents how a philosopher lives his life.
    public void lifeCycleOfPhilosopher(int id) throws InterruptedException {
        while (true) {
            contemplate();
            eat(id);
        }
    }

    //We can sleep the thread when the philosopher is thinking
    void contemplate() throws InterruptedException {
        Thread.sleep(random.nextInt(500));
    }

    //Philosopher trying to eat
    //Philosopher A(0) need forks 4 and 0
    //Philosopher B(1) need forks 0 and 1
    //Philosopher C(2) need forks 1 and 2
    //Philosopher D(3) need forks 2 and 3
    //Philosopher E(4) need forks 3 and 4

    /**
     * This means each thread will also need to tell us what ID it is before we can attempt to lock the appropriate forks
     * for him.  This is why you see the eat() method take in an ID parameter.
     */
    /**
     * We can programmatically express the requirement for each philosopher to hold the right and left forks as follows:
     * forks[id]
     * forks[(id+4) % 5]
     */
    /**
     * Convince yourself that with five forks and fours philosophers deadlock is impossible, since at any point in time,
     * even if each philosopher grab one fork, there will still be one fork left that can be acquired by one of the
     * philosophers to eat.
     */
    void eat(int id) throws InterruptedException {
        //maxDiners allows only 4 philosophers to attempt picking up forks
        maxDiners.acquire();

        //acquire the left fork first
        forks[id].acquire();
        //acquire the right fork second
        forks[(id + 4) % 5].acquire();
        //eat to your heart's content
        System.out.println("Philosopher "+ id + " is eating");
        //release forks for others to use.
        forks[id].release();
        forks[(id + 4) % 5].release();

        maxDiners.release();
    }

    static void startPhilosopher(DiningPhilosophers dp, int id) {
        try {
            dp.lifeCycleOfPhilosopher(id);
        } catch (InterruptedException ie) {

        }
    }

    public static void runTest() throws InterruptedException {
        final DiningPhilosophers diningPhilosophers = new DiningPhilosophers();

        Thread p1 = new Thread(() -> startPhilosopher(diningPhilosophers, 0));
        Thread p2 = new Thread(() -> startPhilosopher(diningPhilosophers, 1));
        Thread p3 = new Thread(() -> startPhilosopher(diningPhilosophers, 2));
        Thread p4 = new Thread(() -> startPhilosopher(diningPhilosophers, 3));
        Thread p5 = new Thread(() -> startPhilosopher(diningPhilosophers, 4));

        p1.start();
        p2.start();
        p3.start();
        p4.start();
        p5.start();

        p1.join();
        p2.join();
        p3.join();
        p4.join();
        p5.join();
    }
}
