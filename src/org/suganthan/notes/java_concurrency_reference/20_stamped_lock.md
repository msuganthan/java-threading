<!-- TOC -->
  * [Overview](#overview)
  * [Modes](#modes)
    * [Reading](#reading)
    * [Writing](#writing)
    * [Optimistic Read](#optimistic-read)
  * [Converting modes](#converting-modes)
    * [Upgrade to write lock after optimistic read](#upgrade-to-write-lock-after-optimistic-read)
    * [Upgrade to write lock from read lock](#upgrade-to-write-lock-from-read-lock)
    * [Downgrade to read lock from write lock](#downgrade-to-read-lock-from-write-lock)
    * [Downgrade to optimistic read from write lock](#downgrade-to-optimistic-read-from-write-lock)
    * [Failed upgrade to write lock example](#failed-upgrade-to-write-lock-example)
  * [Characteristics](#characteristics)
  * [Conclusion](#conclusion)
<!-- TOC -->

## Overview

The `StampedLock` was introduced in Java 9 and is a capability-based lock with three modes for controlling read/write access. The `StampedLock` class is designed for use as an internal utility in the development of other thread-safe components. Its use relies on knowledge of the internal properties of the data, objects, and methods it protects.

## Modes

The state of the `StampedLock` is defined by a version and mode. There are three modes the lock can in:

* Writing 
* Reading
* Optimistic Reading

On acquiring a lock, a stamp(long value) is returned that represents and controls access with respect to the lock state. The stamp can be used later on to release the lock or convert the existing acquired lock to a different mode.

### Reading

The read mode can be acquired using the `readLock()` method. When reading a thread isn't expected to make any changes to the shared state and therefor it makes sense to have multiple threads acquire the read lock at the same time. The method returns a stamp that can used to unlock the read lock. Timed and untimed version of `tryReadLock()` also exist.

The code widget below demonstrates multiple threads acquiring the `StampedLock` in read mode.

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

class Demo {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        StampedLock stampedLock = new StampedLock();

        long readStamp1 = stampedLock.readLock();
        long readStamp2 = stampedLock.readLock();

        try {
            for (int i = 0; i < 3; i++) {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            long readStamp = stampedLock.readLock();
                            System.out.println("Read lock count in spawned thread " + stampedLock.getReadLockCount());
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException ie) {

                            } finally {
                                stampedLock.unlockRead(readStamp);
                            }
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                });
            }

            Thread.sleep(5000);
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);
            
            stampedLock.unlock(readStamp1);
            stampedLock.unlock(readStamp2);
        }

        System.out.println("Read lock count in main thread "+ stampedLock.getReadLockCount());
        System.out.println("stampedLock.isReadLocked() ==> "+stampedLock.isReadLocked())
        
    }
}
```
While read lock is held by a reader thread, all attempts to acquire the write lock will be blocked.

### Writing

The write mode can be acquired by invoking the writeLock() method. **When the write lock is held, all threads attempting to acquire the read lock will be blocked**. Similar to the read lock, timed and untimed versions of tryWriteLock() exist. The following widget demonstrates the use of the write lock:

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

class Demo {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        StampedLock stampedLock = new StampedLock();
        long stamp = stampedLock.writeLock();

        try {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Attempting to acquire read lock");
                    long readStamp = stampedLock.readLock();
                    System.out.println("Read Lock acquired");
                    stampedLock.unlock(readStamp);
                }
            });
            Thread.sleep(3000);
        } finally {
            System.out.println("Write lock being released");
            stampedLock.unlock(stamp);

            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);
        }
    }
}
```

In the above program, the print statement for releasing the write lock is always output before the statement for acquiring the read lock.

Remember that the writeLock() is not re-entrant. The following program results in a deadlock:

```java
class Demonstration {
    public static void main( String args[] ) {
      
        // create an instance of StampedLock
        StampedLock stampedLock = new StampedLock();

        stampedLock.writeLock();

        stampedLock.writeLock();        
    }
}
```
### Optimistic Read

Consider a scenario, where we **never** want to block a thread from acquiring the write lock if the lock isn't acquired. Recall, that if the read lock is already held then a thread attempting to acquire the write lock will block. `StampedLock` makes this scenario possible with the optimistic reading mode. A thread can invoke the `tryOptimisticRead()` method which returns a non-zero value if the write lock isn't exclusively locked. The thread can then proceed to read but once the thread reads the desired value it must validate if the lock wasn't acquired for a write in the meanwhile. If validation returns true then the reader thread can safely assume that from the time it as returned a stamp from invoking the `tryOptimisticRead()` method till the time the thread validate the stamp, the lock hasn't been acquired for a write in the intervening period, then the validation will return false. The validation can be performed using the method `validate(long)` which takes in the stamp issued at the time of invoking `tryOptimisticRead()`

This mode can be broken at any time by a thread acquiring a write lock. Another way to think of this mode is as an extremely weak version of a read-lock. Using optimistic reads in cases where the read happens briefly can improve throughput and reduce contention.

Note that tryOptimisticRead() doesn’t acquire the read lock nor is the read lock count incremented as the following snippet demonstrates:

```java
class Demonstration {
    public static void main( String args[] ) {

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // create an instance of StampedLock
        StampedLock stampedLock = new StampedLock();

        // optimistic read
        stampedLock.tryOptimisticRead();

        // outputs "read lock count 0 is read locked false"
        System.out.println("read lock count " + stampedLock.getReadLockCount() + " is read locked " + stampedLock.isReadLocked());
    }
}
```

The sequence of operations in the following code widget illustrates when a stamp returned from a tryOptimisticRead() call becomes invalid:

```java
class Demonstration {
    public static void main( String args[] ) {
        // create an instance of StampedLock
        StampedLock stampedLock = new StampedLock();

        // try optimistic read
        long stamp = stampedLock.tryOptimisticRead();

        // check for validation, prints true
        System.out.println(stampedLock.validate(stamp));

        // acquire the write lock which will invalidate the previous stamp
        stampedLock.writeLock();

        // check for validation, prints false
        System.out.println(stampedLock.validate(stamp));
    }
}
```

## Converting modes

The `StampedLock` also provides support for converting locks from one mode to another if certain conditions are met. For instance, the method `tryConvertToWriteLock(stamp)` upgrades to a write lock if one of the following is true:

* The write lock is current held.
* The read lock is current held and no other readers exists.
* The optimistic mode is in progress and write lock is available.

### Upgrade to write lock after optimistic read

```java
StampedLock stampedLock = new StampedLock();

long stamp = stampedLock.tryOptimisticRead();

stampedLock.tryConvertToWriteLock(stamp);

System.out.println("Read locks held : "+ stampedLock.getReadLockCount + "\n Write lock held : "+ stampedLock.isWriteLocked());
```

### Upgrade to write lock from read lock

```java
StampedLock stampedLock = new StampedLock();

long stamp = stampedLock.readLock();

stampedLock.tryConvertToWriteLock(stamp);

System.out.println("Read locks held : "+stampedLock.getReadLockCount() + " \nWrite lock held : "+stampedLock.isWriteLocked());
```
### Downgrade to read lock from write lock

```java
StampedLock stampedLock = new StampedLock();

long stamp = stampedLock.writeLock();

stampedLock.tryConvertToReadLock(stamp);

System.out.println("Read locks held : " + stampedLock.getReadLockCount() + "\nWrite lock held : " + stampedLock.isWriteLocked());
```

### Downgrade to optimistic read from write lock

```java
StampedLock stampedLock = new StampedLock();

long stamp = stampedLock.readLock();

stampedLock.tryConvertToOptimisticRead(stamp);

System.out.println("Read locks held : " + stampedLock.getReadLockCount() + "\nWrite lock held : " + stampedLock.isWriteLocked());
```

### Failed upgrade to write lock example

The final example demonstrates a failed attempt by the main thread to upgrade to the write lock from optimistic read mode. Another spawned thread holds the write lock and prevents the main thread from upgrading. The output of the program appears below:

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

class Demo {
    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(10);

        StampedLock stampedLock = new StampedLock();
        long stamp = stampedLock.tryOptimisticRead();

        try {
            service.submit(new Runnable() {
                @Override
                public void run() {
                    long stamp = stampedLock.writeLock();

                    try {
                        Thread.sleep(6000);
                        System.out.println("Spawned thread exiting.");
                    } catch (InterruptedException ie) {
                        //ignore
                    } finally {
                        stampedLock.unlockWrite(stamp);
                    }
                }
            });

            Thread.sleep(3000);
            stamp = stampedLock.tryConvertToWriteLock(stamp);
            System.out.println("Stamp : " + stamp + "\nRead locks held : " + stampedLock.getReadLockCount() + "\nWrite lock held : " + stampedLock.isWriteLocked());
        } finally {
            service.shutdown();
            service.awaitTermination(1, TimeUnit.HOURS);
        }
    }
}

```
## Characteristics

* Similar to `Semaphore` the `StampedLock` has no notion of ownership. Lock acquired by one thread can be released by another thread, which isn't possible for implementation of the `Lock` interface
* The `StampedLock` class doesn't implement the `Lock` or `ReadWriteLock` interfaces but applications that desire to use the functionality offered by these interfaces out of an instance of `StampedLock` can do so using methods `asReadLock()` `asWriteLock()` and `asReadWriteLock()`. The following statements are possible

```java
StampedLock stampedLock = new StampedLock();

Lock readLock = stampedLock.asReadLock();

Lock writeLock = stampedLock.asWriteLock();

ReadWriteLock readWriteLock = stampedLock.asReadWriteLock();

```

* The scheduling policy of `StampedLock` does not consistently prefer readers over writers or vice versa. All `try*` methods are best-effort and do not necessarily conform to any scheduling or fairness policy.
* A zero return from any `try*` method for acquiring or converting locks does not imply or carry any information about the state of the lock; a subsequent invocation may succeed.
* Stamp values can recycle after a year but not earlier. This implies that if a stamp is head without use or validation for longer then this period may fail to validate correctly.
* The `StampedLock` class is serializable but always deserializes into the initial unlocked state, so its instances are not useful for remote locking.
* Stamps aren’t cryptographically secure and a valid stamp can be guessed by malicious participants in a system.
* The StampedLock’s write lock is not reentrant i.e. recursively attempting to acquire the lock will result in a deadlock as the following widget demonstrates. The execution times out because the main thread deadlocks itself by acquiring the write lock twice.

```java
import java.util.concurrent.*;

class Demonstration {
    public static void main( String args[] ) {
        
        // create an instance
        StampedLock stampedLock = new StampedLock();

        stampedLock.writeLock();
        stampedLock.writeLock();

    }
}
```
## Conclusion