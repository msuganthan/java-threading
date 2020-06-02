package org.suganthan.executorFramework;

import java.util.concurrent.Executor;

public class ThreadExecutorExample {
    public static void main(String[] args) {
        DumbExecutor dumbExecutor = new DumbExecutor();
        MyTask task = new MyTask();
        dumbExecutor.execute(task);
    }

    static class DumbExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            Thread newThread = new Thread(command);
            newThread.start();
        }
    }

    static class MyTask implements Runnable {
        @Override
        public void run() {
            System.out.println("My task is running now");
        }
    }
}
