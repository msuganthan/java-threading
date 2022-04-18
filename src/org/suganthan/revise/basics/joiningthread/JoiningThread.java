package org.suganthan.revise.basics.joiningthread;

public class JoiningThread {
    public static void main(String[] args) throws InterruptedException {
        ExecuteMe executeMe = new ExecuteMe();
        Thread innerThread = new Thread(executeMe);
        /**
         * A daemon thread runs in the background but as soon as the main application thread exits, all
         * daemon thread are killed by the JVM.
         *
         * That in case a spawned thread isn't marked
         * as daemon then even if the main thread finishes
         * execution, JVM will wait for the spawned thread
         * to finish before tearing down the process.
         */
        innerThread.setDaemon(true);
        innerThread.start();
        innerThread.join();
        /**
         * If we remove the above join statement, then the
         * main thread may print its statement before
         * the innerThread is done executing.
         */
        System.out.println("Main thread exiting..");
    }
}

class ExecuteMe implements Runnable {
    @Override
    public void run() {
        System.out.println("Say hello over and over again");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.out.println("Interrupted ...");
            e.printStackTrace();
        }
    }
}
