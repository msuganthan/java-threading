package org.suganthan.threadingProblems.uniSexBathRoom;

/**
 * A bathroom is being designed for the use of both of males and females in an office but requires the following constraints
 * to be maintained:
 *
 * *. There cannot be men and women in the bathroom at the same time.
 * *. There should never be more than three employees in the bathroom simultaneously.
 *
 * The solution should avoid deadlocks. For now, though, don't worry about starvation.
 */
public class Demonstration {

    public static void main(String[] args) throws InterruptedException {
        runTest();
    }

    public static void runTest() throws InterruptedException {
        final UnisexBathroom unisexBathroom = new UnisexBathroom();

        Thread female1 = new Thread(() -> {
            try {
                unisexBathroom.femaleUseBathroom("Lisa");
            } catch (InterruptedException ie) {

            }
        });

        Thread male1 = new Thread(() -> {
            try {
                unisexBathroom.maleUseBathroom("John");
            } catch (InterruptedException ie) {

            }
        });

        Thread male2 = new Thread(() -> {
            try {
                unisexBathroom.maleUseBathroom("Bob");
            } catch (InterruptedException ie) {

            }
        });

        Thread male3 = new Thread(() -> {
            try {
                unisexBathroom.maleUseBathroom("Anil");
            } catch (InterruptedException ie) {

            }
        });

        Thread male4 = new Thread(() -> {
            try {
                unisexBathroom.maleUseBathroom("Wentao");
            } catch (InterruptedException ie) {

            }
        });

        female1.start();
        male1.start();
        male2.start();
        male3.start();
        male4.start();

        female1.join();
        male1.join();
        male2.join();
        male3.join();
        male4.join();
    }
}

class UnisexBathroom {
    final String WOMEN = "women";
    final String MEN   = "men";
    final String NONE  = "none";

    String inUseBy     = NONE;
    int empsInBathroom = 0;

    /**
     * Making the method synchronized, while one male thread is accessing the bathroom, then another one can't access
     * the bath even though the problem says that more than one male should be able to use the bathroom.
     * @param name
     * @throws InterruptedException
     */
    void maleUseBathroom(String name) throws InterruptedException {
        synchronized (this) {
            while (inUseBy.equals(WOMEN)) {
                //The wait call will give up the monitor associated with the object, giving other threads a chance to
                // acquire it
                this.wait();
            }
            empsInBathroom++;
            inUseBy = MEN;
        }

        useBathroom(name);

        synchronized (this) {
            empsInBathroom--;

            if (empsInBathroom == 0) inUseBy = NONE;

            //Since we might have just updated the value of inUseBy, we should notifyAll waiting threads.
            this.notifyAll();
        }
    }

    void femaleUseBathroom(String name) throws InterruptedException {
        synchronized (this) {
            while (inUseBy.equals(MEN))
                this.wait();

            empsInBathroom++;
            inUseBy = WOMEN;
        }

        useBathroom(name);

        synchronized (this) {
            empsInBathroom--;

            if (empsInBathroom == 0) inUseBy = NONE;
            //Since we might have just updated the value of inUseBy, we should notifyAll waiting threads.
            this.notifyAll();
        }
    }

    void useBathroom(String name) throws InterruptedException {
        System.out.println(name + " using bathroom. Current employee in bathroom = "+empsInBathroom);
        Thread.sleep(10000);
        System.out.println(name + " done using bathroom ");
    }
}
