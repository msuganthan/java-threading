<!-- TOC -->
  * [ReadWriteLock](#readwritelock)
    * [ReentrantReadWriteLock](#reentrantreadwritelock)
    * [Fair Mode](#fair-mode)
    * [Cache Example](#cache-example)
    * [Downgrading to Read Lock](#downgrading-to-read-lock)
    * [Reentrancy](#reentrancy)
<!-- TOC -->

## ReadWriteLock

The `ReadWriteLock` interface is part of Java's `java.util.concurrent.locks` package. The only implementing class for the interface is `ReentrantReadWriteLock`. The `ReentrantReadWriteLock` can be locked by **multiple readers at the same while writer threads have to wait.** Conversely, the `ReentrantReadWriteLock` can be locked by a **single writer thread at a time and other writer or reader threads have to wait for the lock to be free.**

### ReentrantReadWriteLock

The `ReentrantReadWriteLock` as the name implies allows threads to recursively acquire the lock. Internally there are two locks to guard the read and write accesses. `ReentrantReadWriteLock` can help improve concurrency over using a mutual exclusion lock as it allows **multiple readers to read concurrently.** However, whether an application will truly realize concurrency improvements depends on other factor such as:

* Running on multiprocessor machines
* Frequency of reads and writes. Generally `ReadWriteLock` can improve concurrency in scenarios where read operations occurs frequently and write operations are infrequent. If write operations happens often then most of the time is spent with the lock acting as a mutual exclusion lock.
* Contention for data: i.e. the number of threads that try to read or write at the same time.
* Duration of the read and write operations. If read operations are very short then the overhead of locking `ReadWriteLock` versus a mutual exclusion lock can be higher.

In practice, you'll need to evaluate the access patterns to the shared data in your application to determine the suitability of using the `ReadWriteLock`

### Fair Mode

The `ReentrantReadWriteLock` can also be operated in the fair mode, which grants entry to threads in an approximate arrival order. The longest waiting writer thread or a group of longest waiting threads is given preference to acquire the lock when it becomes free. **In case of reader threads we consider a group since multiple reader threads can acquire the lock concurrently.**

### Cache Example

```java

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Demo {
    static Random random = new Random();

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(15);

        //cache
        HashMap<String, Object> cache = new HashMap<>();
        ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        cache.put("key", -1);

        Runnable writerTask = new Runnable() {
            @Override
            public void run() {
                writerThread(cache, readWriteLock);
            }
        };

        Runnable readerTask = new Runnable() {
            @Override
            public void run() {
                readerThread(cache, readWriteLock);
            }
        };

        try {
            Future future1 = executorService.submit(writerTask);
            Future future2 = executorService.submit(readerTask);
            Future future3 = executorService.submit(readerTask);
            Future future4 = executorService.submit(readerTask);
            Future future5 = executorService.submit(readerTask);
        } finally {
            executorService.shutdown();
        }
    }

    static void writerThread(HashMap<String, Object> cache, ReadWriteLock lock) {
        for (int i = 0; i < 9; i++) {
            try {
                Thread.sleep(random.nextInt(50));
            } catch (InterruptedException ie) {
                //ignore
            }
            lock.writeLock().lock();
            try {
                System.out.println("Acquired write lock");
                cache.put("key", random.nextInt(1000));
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    static void readerThread(HashMap<String, Object> cache, ReadWriteLock readWriteLock) {
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException ie) {
                //ignore
            }
            readWriteLock.readLock().lock();
            try {
                System.out.println("Acquire read lock and reading key = " + cache.get("key"));
            } finally {
                readWriteLock.readLock().unlock();
            }
        }
    }
}
```

### Downgrading to Read Lock

To showcase downgrading from a write lock to a read lock, we'll slightly modify our scenario to include a challenging requirement that readers can't tolerate stale data. We'll assume that reader have the ability to trigger an update of the cache data if it found to be stale. A boolean flag `isDataFresh` depicts whether data in the cache is fresh or not. We'll make changes to our previous program to accommodate for these new requirements. The writer thread will now occasionally set the flag `isDataFresh` to false, to indicate that the reader threads must trigger an update. The writer thread from our previous program doesn't write data to the cache anymore rather it simply sets the flag `isDataFresh` to false, to force the reader threads to trigger an update.

```java
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Demo {

    static Random random = new Random();
    static boolean isDataFresh = true;

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(15);

        HashMap<String, Object> cache = new HashMap<>();
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        cache.put("key", -1);

        Runnable writerTask = new Runnable() {
            @Override
            public void run() {
                writerThread(lock);
            }
        };

        Runnable readerTask = new Runnable() {
            @Override
            public void run() {
                readerTask(lock);
            }
        };

        try {
            Future future1 = executorService.submit(writerTask);
            Future future2 = executorService.submit(readerTask);
            Future future3 = executorService.submit(readerTask);
            Future future4 = executorService.submit(readerTask);

            future1.get();
            future2.get();
            future3.get();
            future4.get();
        } finally {
            executorService.shutdown();
        }
    }

    static void writerThread(ReadWriteLock lock) {
        for (int i = 0; i < 9; i++) {
            try {
                Thread.sleep(random.nextInt(50));
            } catch (InterruptedException ie) {
                //ignore
            }
            lock.writeLock().lock();
            System.out.println("Acquired write lock.");
            isDataFresh = false;
            lock.writeLock().lock();
        }
    }

    static void readerThread(Map<String, Object> cache, ReadWriteLock lock) {
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(random.nextInt(50));
            } catch (InterruptedException ie) {
                //ignore
            }
            
            lock.readLock().lock();
            try {
                if (!isDataFresh) {
                    lock.readLock().unlock();
                    lock.writeLock().lock();
                    try {
                        if (!isDataFresh) {
                            updateData(cache);
                        }
                        lock.readLock().lock();
                    } finally {
                        lock.writeLock().unlock();
                    }
                }
                System.out.println("Acquire read lock and reading key = "+ cache.get("key"));
            } finally {
              lock.readLock().unlock();  
            }
        }
    }
    
    static void updateData(Map<String, Object> cache) {
        cache.put("key", random.nextInt(1000));
        isDataFresh = true;
    }
}
```

### Reentrancy

The `ReentrantReadWriteLock` allows thread to recursively acquire the read or write lock. You must remember to unlock as many times as you lock. The sequence of locks in snippet below will result in a deadlock since the write lock is acquired twice but htleased only once before attempting to acquire the read lock.

```java
ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
lock.writeLock().lock();
lock.writeLock().lock();
lock.writeLock().unlock();
lock.readLock().lock();
```