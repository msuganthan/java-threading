<!-- TOC -->
  * [CyclicBarrier](#cyclicbarrier)
<!-- TOC -->

## CyclicBarrier

`CyclicBarrier` is a synchronization mechanism that allows multiple threads to wait for each other at a common point before continuing execution. The threads wait for each other by calling the `await()` method on the `CyclicBarrier`.

`CyclicBarrier` is initialized with an integer that denotes the number of threads that need to call the `await()` method on the barrier. Second argument is `CyclicBarrier's` constructor is a `Runnable` instance that includes the action to be executed once the last thread arrives.

The most useful property of `CyclicBarrier` is that it can be reset to its initial state by calling the `reset()` method. It can be **reused** after all the threads have been released.

```java
import org.suganthan.miscellaneous.volatile_.TaskRunner;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

class Task implements Runnable {
    private CyclicBarrier barrier;

    public Task(CyclicBarrier barrier) {
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            System.out.println(Thread.currentThread().getName() + " is waiting on barrier");
            barrier.await();
            System.out.println(Thread.currentThread().getName() + " has crossed the barrier.");
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (BrokenBarrierException ex) {
            ex.printStackTrace();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(3, new Runnable() {
            @Override
            public void run() {
                System.out.println("All parties have arrived at the barrier, lets continue execution.");
            }
        });

        Thread t1 = new Thread(new Task(cyclicBarrier), "Thread 1");
        Thread t2 = new Thread(new Task(cyclicBarrier), "Thread 2");
        Thread t3 = new Thread(new Task(cyclicBarrier), "Thread 3");
        
        t1.start();
        t2.start();
        t3.start();
    }
}
```