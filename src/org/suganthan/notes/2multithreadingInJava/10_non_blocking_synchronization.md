### Introduction

**Much of the performance improvements seen in classes such as `Semaphore` and `ConcurrentLinkedQueue` versus their synchronized equivalents come from the use of atomic variables and non-blocking synchronization. Non-blocking algorithms use machine-level atomic instructions such as compare-and-swap instead of locks to provide data integrity when multiple threads access shared resources**. Non-blocking algorithms are harder to design and implement but out perform lock-based alternatives in terms of liveness and scalability. As the name suggests, non-blocking algorithms don't block when multiple threads contend for the same data and as a consequence greatly reduce scheduling overhead. These algorithms don't suffer from deadlocks, liveness issues and individual thread failures. More formally:

1. **An algorithm is called non-blocking if the failure or suspension of a thread doesn't cause the failure or suspension of another thread.**
2. **An algorithm is called lock free if at every step of the algorithm some thread participant of the algorithm can make progress.**

### Nonblocking counter

Designing a thread-safe counter would require using locks so that threads don't step over each other. An increment operation on a `long` variable consists three steps. Fetching the current value, incrementing it and then writing it back. All three have to be executed atomically to achieve thread-safety. A lock-based implementation of the lock appears below:

```java
class LockBasedCounter {
    private long value;
    
    public synchronized long getValue() {
        return value;
    }
    
    public synchronized void increment() {
        value++;
    }
}
```

Locking comes with its baggage and we can design a counter that doesn't cause threads to be suspended in the face of contention. To build such a counter, we'll need the hardware to support the CAS instruction. For out purposes we'll write a class `SimulatedCAS` that imitates the CAS instruction. 

```java
class SimulatedCAS {
    private long value = 0;
    
    public SimulatedCAS(long initValue) {
        value = initValue;
    }
    
    synchronized long getValue() {
        return value;
    }
    
    synchronized long compareAndSwap(long expectedValue, long newValue) {
        if (value == expectedValue) {
            value = newValue;
            return expectedValue;
        }
        return value;
    }
    
    synchronized boolean compareAndSet(long expectedValue, long newValue) {
        return compareAndSwap(expectedValue, newValue) == expectedValue;
    }
}

class NonBlockingCounter {
    private SimulatedCAS count = new SimulatedCAS(0);
    
    public long get() {
        return count.getValue();
    }
    
    public void increment() {
        long currentCount;
        do {
            currentCount = count.getValue();
        } while (currentCount != count.compareAndSwap(currentCount, currentCount + 1));
    }
}
```

**CAS-based counter performance**

The performance of CAS instruction varies across processors and architectures and though it may seem that a CAS-based counter may perform poorly in comparision to a lock-based counter, the reality is otherwise. In practice CAS-based locks outperform lock-based counters when there is no contention (as the thread doesn’t go through the process of acquiring a lock) and often when there is low to moderate contention.