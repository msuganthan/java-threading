package org.suganthan.miscellaneous.threadSafety;

/**
 * We synchronize on a Boolean object in the first thread but sleep before we call wait() on the object.
 * While the first thread is asleep, the second thread goes on to the change the flag's value. When the first thread
 * wakes up and attempts to invoke wait(), it is met with an change in flag's value.
 *
 * When the first wakes up and attempts to invoke wait(), it is met with a IllegalMonitorState exception. The object
 * the first thread synchronized on before going to sleep has been changed and now it is attempting to call wait() on
 * an entirely different object without having synchronized on it.
 */
public class IncorrectSynchronization {
    public static void main(String[] args) throws InterruptedException {
        Incorrect.runExample();
    }
}


class Incorrect {
    Boolean flag = new Boolean(true);

    public void example() throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            synchronized (flag) {
                try {
                    while (flag) {
                        System.out.println("First thread about to sleep");
                        Thread.sleep(5000);
                        System.out.println("Woke up and about to invoke wait()");
                        flag.wait();
                    }
                } catch (InterruptedException interruptedException) {

                }
            }
        });

        Thread thread2 = new Thread(() -> {
           flag = false;
           System.out.println("Boolean assignment done");
        });

        thread1.start();
        Thread.sleep(1000);
        thread2.start();
        thread1.join();
        thread2.join();
    }

    public static void runExample() throws InterruptedException {
        Incorrect incorrect = new Incorrect();
        incorrect.example();
    }
}