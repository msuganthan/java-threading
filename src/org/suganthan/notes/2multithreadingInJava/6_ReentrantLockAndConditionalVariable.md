**ReEntrant Lock:**

Java's answer to the traditional mutex is the reentrant lock, which comes with additional bells and whistles. It is similar to the implicit monitor lock accessed when using `synchronized` methods or blocks. With the reentrant lock, you are free to lock and unlock it in different methods **but not with** different threads. If you attempt to unlock a reentrant lock object by a thread which didn't lock it initially, you'll get an **IllegalMonitorStateException.** This behavior is similar to when a thread attempts to unlock a thread mutex.

**Condition Variables:**

We saw how each java object exposes the three method `wait()`, `notify()` and `notifyAll()` which can be used to suspend threads till some conditions become true. You can think of Condition as factoring out these three methods of the object monitor into separate objects so that there can be multiple wait-sets per objects. As a reentrant lock replace `synchronized` blocks oe methods, a condition replaces the object monotor methods. In the same vein, one can't invoke the condition vairable's method without acquiring the associated lock, just like one can't wait on an object's monitor with synchronizing on the object first In face, a reentrant lock expoeses an API to create new condition variables, like so:

```java
Lock lock = new ReentrantLock();
Condition condition = lock.newCondition();
```

Notice, **how we can now have multiple condition variables associated with the same lock.** In the `synchronized` paradigm, we could only have one wait-set associated with each object.
