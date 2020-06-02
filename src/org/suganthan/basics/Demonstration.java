package org.suganthan.basics;

public class Demonstration {
    public static void main(String[] args) throws InterruptedException {
        ExecuteMe executeMe = new ExecuteMe();
        Thread innerThread = new Thread(executeMe);
        /**
         * Daemon thread runs in the background but as soon as the main application thread exits,
         * all damon threads are killed by the JVM.
         *
         * Note that in case a spawned thread isn't marked as deamon then even if the main thread
         * finishes execution, the JVM will wait for the spawed thread to finish before tearing
         * down the process
         */

        innerThread.setDaemon(true);
        innerThread.start();
        /**
         * What happens if the main thread finishes execution before the inner thread?
         */
        /**
         * If we want the main thread to wait for the inner thread to finish before
         * proceeding forward, we can direct the main thread to suspend its execution
         * by calling join method on the innerThread object right after we start the
         * inner thread.
         * innerThread.join();
         */
        //innerThread.join();
        System.out.println("Main thread exiting.");
    }
}

class ExecuteMe implements Runnable {
    @Override
    public void run() {
        while (true) {
            System.out.println("Say Hello over and over again");
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {}
        }
    }
}