package org.suganthan;

/**
 * Reference: https://www.youtube.com/watch?v=SC2jXxOPe5E
 */
public class VolatileTestProgram {

    private static boolean running = false;
    //private static volatile boolean running = false;

    public static void main(String[] args) throws Exception{

        //Starting a new thread.
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Wait for running to become true.
                while (!running) {}
                System.out.println("Started...");

                //Wait for running to become false.
                while (running) {}
                System.out.println("Stopped...");
            }
        }).start();

        //Wait for one second
        Thread.sleep(3000);
        System.out.println("Starting.");
        //Set running to true
        running = true;

        //Wait for one second
        Thread.sleep(3000);
        System.out.println("Stopping.");
        //Set running to false
        running = false;
    }
}
