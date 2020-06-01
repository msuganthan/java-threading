package org.suganthan;

/**
 * Reference: https://www.youtube.com/watch?v=SC2jXxOPe5E
 *
 * Here you are having two threads
 * 1. main-thread
 * 2. child-thread
 * Based on the variable RUNNING, the child thread would start its execution.
 *
 * 1. With the normal declaration, when the main thread changes the RUNNING variable value it won't be visible to child thread.
 *
 * 2. Whereas if the variable RUNNING is declared as volatile the change would reflect to the child thread immediately.
 *
 * Reason: If the variable RUNNING is declared normal variable, the changes are visible only in local memory
 * If the variable is declared as volatile, the changes are available in common memory.
 */
public class VolatileTestProgram {

    //private static boolean running = false;
    private static volatile boolean RUNNING = false;

    public static void main(String[] args) throws Exception{

        //Starting a new thread.
        new Thread(() -> {
            //Wait for running to become true.
            while (!RUNNING) {}
            System.out.println("Started...");

            //Wait for running to become false.
            while (RUNNING) {}
            System.out.println("Stopped...");
        }).start();

        //Wait for one second
        Thread.sleep(3000);
        System.out.println("Starting.");
        //Set running to true
        RUNNING = true;

        //Wait for one second
        Thread.sleep(3000);
        System.out.println("Stopping.");
        //Set running to false
        RUNNING = false;
    }
}
