**Missed Signal:**

A missed signal happens when a signal is sent by a thread before the other thread start waiting on a condition. This is exemplified by the following code snippet.

Missed signals are caused by using the wrong concurrency constructs. In the below example, a condition variable is used to coordinate between the signaller and the waiter thread. The condition is signaled at a time when no thread is waiting on it causing a missed signal.

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class MissedSignalExample {
    public static void example() throws InterruptedException {
        final ReentrantLock lock = new ReentrantLock();
        final Condition condition = lock.newCondition();

        Thread signaller = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                condition.signal();
                System.out.println("Sent signal");
                lock.unlock();
            }
        });

        Thread waiter = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.unlock();
                try {
                    condition.await();
                    System.out.println("Received signal");
                } catch (InterruptedException ie) {
                    
                }
                lock.unlock();
            }
        });
        
        signaller.start();;
        signaller.join();
        
        waiter.start();
        waiter.join();

        System.out.println("Program Exiting");
    }
}
```

Apart from refactoring the code to match the idiomatic usage of condition variables in a while loop, the other possible fix is to use a semaphore for signalling between the two threads as shown below

```java
import java.util.concurrent.Semaphore;

class FixedMissedSignalExample {
    public static void example() throws InterruptedException {
        final Semaphore semaphore = new Semaphore(1);
        Thread signaller = new Thread(new Runnable() {
            @Override
            public void run() {
                semaphore.release();
                System.out.println("Sent signal");
            }
        });

        Thread waiter = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();
                    System.out.println("Received signal");
                } catch (InterruptedException ie) {
                    
                }
            }
        });
        
        signaller.start();
        signaller.join();
        
        Thread.sleep(5000);
        
        waiter.start();
        waiter.join();

        System.out.println("Program Exiting");
    }
}
```