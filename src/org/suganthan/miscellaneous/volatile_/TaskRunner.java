package org.suganthan.miscellaneous.volatile_;

public class TaskRunner {
    private boolean ready;

    public static void main(String[] args) {
        new TaskRunner().test();
    }

    void test() {
        new Reader().start();
        new Modifier().start();
    }

    class Reader extends Thread {
        @Override
        public void run() {
            while(!ready) {
                System.out.println("I am waiting...");
                Thread.yield();
            }
            System.out.println("Print something");
        }
    }

    class Modifier extends Thread {
        @Override
        public void run() {
            System.out.println("I am changing the value ready");
            ready = true;
        }
    }
}


