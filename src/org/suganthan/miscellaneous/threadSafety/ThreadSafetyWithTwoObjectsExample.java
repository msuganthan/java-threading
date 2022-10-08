package org.suganthan.miscellaneous.threadSafety;

public class ThreadSafetyWithTwoObjectsExample {

    /**
     * First and third thread execution happens at the same time.
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        final EmployeeSafetyWithTwoObjects employee1 = new EmployeeSafetyWithTwoObjects();

        Thread thread1 = new Thread(() -> {
            System.out.println("Invoking first thread");
            try {
                employee1.setName("Sugan");
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            System.out.println("First thread completed its execution");
        });

        Thread thread2 = new Thread(() -> {
            System.out.println("Invoking second thread");
            try {
                employee1.resetName();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            System.out.println("Second thread completed its execution");
        });

        Thread thread3 = new Thread(() -> {
            System.out.println("Invoking Third thread");
            try {
                employee1.getName();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            System.out.println("Third thread completed its execution");
        });

        thread1.start();
        thread2.start();
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();


    }
}

class EmployeeSafetyWithTwoObjects {
    String name;
    Object lock = new Object();

    public synchronized void setName(String name) throws InterruptedException {
        Thread.sleep(10000);
        this.name = name;
    }

    public synchronized void resetName() throws InterruptedException {
        Thread.sleep(10000);
        this.name = "";
    }

    public String getName() throws InterruptedException {
        synchronized (lock) {
            Thread.sleep(10000);
            return this.name;
        }
    }

}
