<!-- TOC -->
  * [CountdownLatch](#countdownlatch)
<!-- TOC -->

## CountdownLatch

This can be used to block a single or multiple threads while other threads complete their operations.

A `CountDownLatch` object is initialized with the number of tasks/threads it is required to wait for. Multiple threads can block and wait for the `CountDownLatch` object to reach zero by invoking `await()`. Every time a thread finishes its work, the thread invokes `countDown()` which decrements the counter by 1. Once the count reaches the zero, threads waiting on the `await()` methods are notified and resume execution.

The counter in the `CountDownLatch` cannot be reset making the `CountDownLatch` object unreusable. A `CountDownLatch` initialized with a count of 1 serves as an on/off switch where a particular thread is simply waiting for its only partner to complete. Whereas a `CountDownLatch` object initialized with a count of N indicates a thread waiting for N threads to complete their work. However, a single thread can also invoke `countDown()` N times to unblock a thread more than once.

If the `CountDownLatch` is initialized with zero, the thread would not wait for any other threads to complete. The count passed is basically the number of times `countDown()` must be invoked before thread can pass through `await()`. If the `CountDownLatch` has reached zero and `countDown()` is again invoked, the latch will remain release hence making no difference.

The thread blocked on `await()` can also be interrupted by another thread as long as it is waiting and the counter has not reached zero.

```java
import javax.swing.text.MaskFormatter;
import java.util.concurrent.CountDownLatch;

public class Worker extends Thread {
    private CountDownLatch countDownLatch;

    public Worker(CountDownLatch countDownLatch, String name) {
        super(name);
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        System.out.println("Worker " + Thread.currentThread().getName() + " started.");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        System.out.println("Worker " + Thread.currentThread().getName() + " finished");
        countDownLatch.countDown();
    }
}

class Master extends Thread {

    public Master(String name) {
        super(name);
    }

    @Override

    public void run() {
        System.out.println("Master executed " + Thread.currentThread().getName());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        
        Worker A = new Worker(countDownLatch, "A");
        Worker B = new Worker(countDownLatch, "B");
        
        A.start();
        B.start();
        
        countDownLatch.await();
        
        Master D = new Master("Master executed...");
        D.start();
    }
}
```