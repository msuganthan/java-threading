**Waking-up for no reason:**

A spurious wakeup means a thread is woken up even though no signal has been received. Spurious wakeups are a reality and are one of the reasons why the pattern for waiting on a condition variable happens in a while loop as discussed in earlier chapters. There are technical reason beyond our current scope as to why spurious wakeups happen, but for curious on POSIX based operating system when a process is signalled, all its waiting threads are woken up. Below comment is a directly lifted from Java's documentation for the `wait(long timeout)` method.

```java
/**
 * A thread can also wake up without being notified, interrupted or timing out, a so-called <i>spurious wakeup</i>. While this will rarely occur in practice, application must guard against it by testing for the condition that should caused the thread to be awakened and continuing to wait `if` the condition is not satisfied. In other words waits should always occur in loops, like this one:
 * 
 */
    synchronized(obj) {
        while(condition does not hold) 
            obj.wait(timeout);
        ... //Perform action appropriate to condition.
    }
```