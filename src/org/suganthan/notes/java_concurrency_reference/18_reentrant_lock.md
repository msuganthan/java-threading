## ReentrantLock

The `ReentrantLock` implements the `Lock` interface and is functionally similar to the implicit monitor lock accessed using synchronized methods and statements.

The Lock is said to be owned by the thread that locks it and any other thread attempting to lock the object will block. **A thread that already owns the lock will return immediately if it involves lock again**. The reentrant behavior of the lock allows recursively locking by the already owning thread, however the lock supports a maximum of 2147483647 locks by the same thread.

### Idiomatic Use of Lock

Threads can experience deadlocks when locks aren't unlocked after use. The correct idiomatic usage of a lock should follow the below patternL

```java
lock.lock();
try {
    
} finally {
    lock.unlock();    
}
```

If you acquire a lock and then continue execution without a try block it is possible that an exception occurs and the lock is never released even though the program continues to execute. Depending on how the program is structured it is possible that the program experiences a deadline as it progresses.

### Fairness

The ReentrantLock can also be operated in fair mode where the lock is granted to the longest waiting thread. Thus no thread experiences starvation and the variance in times to obtain the lock is also small. Without the fair mode the lock doesn’t guarantee the order in which threads acquire the lock. When a lock is operated in fair mode in an environment with several threads contending access to the lock, throughput suffers and is significantly reduced.

### Example

```java

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Demo {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        ReentrantLock reentrantLock = new ReentrantLock();
        Runnable threadA = new Runnable() {
            @Override
            public void run() {
                threadA(reentrantLock);
            }
        };

        Runnable threadB = new Runnable() {
            @Override
            public void run() {
                threadB(reentrantLock);
            }
        };

        try {
            reentrantLock.lock();
            reentrantLock.lock();
            reentrantLock.lock();

            System.out.println("Main thread lock hold count = " + reentrantLock.getHoldCount());

            Future future1 = executorService.submit(threadA);
            Future future2 = executorService.submit(threadB);

            for (int i = 0; i < 3; i++) {
                Thread.sleep(50);
                reentrantLock.unlock();
            }

            System.out.println("Main thread released lock. Lock hold count = "+reentrantLock.getHoldCount());
            future1.get();
            future2.get();
        } finally {
            for (int i = 0; i < reentrantLock.getHoldCount(); i++) {
                reentrantLock.unlock();
            }
            
            executorService.shutdown();
        }
    }

    static void threadB(Lock lock) {
        lock.lock();
        lock.unlock();
    }

    static void threadA(ReentrantLock lock) {
        String name = "THREAD-A";
        Thread.currentThread().setName(name);
        boolean keepTrying = true;
        System.out.println("Is lock owned by any other thread = " + lock.isLocked());
        while (keepTrying) {
            System.out.println(name + " trying to acquire lock");
            if (lock.tryLock()) {
                try {
                    System.out.println(name + " acquired lock");
                    keepTrying = false;
                } finally {
                    lock.unlock();
                    System.out.println(name + "release lock");
                }
            } else {
                System.out.println(name + " failed to acquire lock. Other threads waiting = " + lock.getQueueLength());
            }

            try {
                Thread.sleep(20);
            } catch (InterruptedException ie) {
                //ignore exception
            }
        }
    }
}

```

Consider the above example which has three thread, the main thread, threadA, and threadB interacting with an instance of `ReentrantLock`. 

The main thread locks the lock object thrice and invokes `unlock` on the object with an artificially introduced delay.

During this time, `threadA` does a busy spinning waiting for the lock to be free. `threadA` uses the method `tryLock()` to check if it can acquire the lock. On the other hand `threadB` simply acquires and then releases the lock.

`threadB` will inevitably block at acquiring the lock and the method `getQueueLength()` will display a count of 1.

The example also demonstrates the use of the method `getHoldCount()` which either returns 0 if the lock isn't held by the thread invoking the method or the number of times the owning thread has recursively acquired the lock. Pay attention to how we use this method in the `finally` block of the main thread to `unlock` as many times as required if an exception occurs.

Finally, there’s the `isLocked()` method that returns `true` if the lock is held by any thread. However, note that both methods `isLocked()` and `getQueueLength()` are designed for monitoring the state of the system and shouldn’t be used in program control, e.g. you should never do something like below:


