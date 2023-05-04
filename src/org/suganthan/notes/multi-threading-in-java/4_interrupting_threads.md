**Interrupting Threads:**

When a thread `wait()`-s or `sleep()`-s then one way for it to give up waiting/sleeping is to be interrupted. If a thread is interrupted while waiting/sleeping, it will wake up and immediately throw Interrupted Exception.

The thread class exposes the `interrupt()` method which can be used to interrupt a thread that is **blocked** in a `sleep()` or `wait()` call. **Note that invoking the interrupt method only sets a flag that is polled periodically by sleep or wait to know the current thread has been interrupted** and an interrupted exception should be thrown.

```java
class InterruptExample {
    static public void example() throws InterruptedException {
        final Thread sleepyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("I am too sleepy... Let me sleep for an hour.");
                    Thread.sleep(1000 * 60 * 60);
                } catch (InterruptedException e) {
                    System.out.println("The interrupt flag is cleared : " + Thread.interrupted() + " " + Thread.currentThread().isInterrupted());
                    Thread.currentThread().interrupt();
                    System.out.println("Oh someone woke me up !!!");
                    System.out.println("The interrupt flag is set now : "+ Thread.currentThread().isInterrupted() + " " + Thread.interrupted());
                }
            }
        });
        sleepyThread.start();
        System.out.println("About to wake up the sleepy thread ...");
        sleepyThread.interrupt();
        System.out.println("woke up sleepy thread ...");
        sleepyThread.join();
    }
}

```

* Once the interrupted exception is thrown, the interrupt flag is cleared.
* The line `Thread.currentThread().interrupt()` again interrupt the thread and no exception is thrown. This is to emphasize that merely calling the interrupt method isn't responsible for throwing the interrupted exception. Rather the implementation should periodically check for the interrupt status and take appropriate action.
* Then we print the interrupt status for the thread, which is set to true because of above line.
* Note that there are two methods to check for the interrupt status of a thread. One is the static method `Thread.interrupted()` and the other is `Thread.currentThread().isInterrupted()`. The important difference between the two is that static method would return the interrupt status and also clear it as the same time.