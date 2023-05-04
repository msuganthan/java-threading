package org.suganthan.threadingproblems.basics.interruptingthread;

public class HelloWorld {

    public static void main(String[] args) throws InterruptedException {
        ExecuteMe1 executeMe1 = new ExecuteMe1();
        Thread innerThread = new Thread(executeMe1);
        innerThread.start();

        System.out.println("Main thread sleep at "+ + System.currentTimeMillis() / 1000);
        Thread.sleep(5000);
        /**
         * Imagine a situation where if a rogue thread sleeps forever or goes into an infinite loop.
         */
        innerThread.interrupt();
        System.out.println("Main thread exiting at "+ +System.currentTimeMillis() / 1000);
    }

    static class ExecuteMe1 implements Runnable {
        @Override
        public void run() {
            try {
                System.out.println("Inner Thread goes to sleep at "+ System.currentTimeMillis() / 1000);
                Thread.sleep(1000 * 1000);
            } catch (InterruptedException ie) {
                System.out.println("Inner Thread interrupted at "+ +System.currentTimeMillis() / 1000);
            }
        }
    }
}
