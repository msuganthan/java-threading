**ReentrantLock**: implements the `Lock` interface and is functionally similar to the implicit monitor lock accessed using `synchronized` methods and statements.

The lock is said to be owned by the thread that `lock`s it and any other thread attempting to `lock` the object will block. **A thread that already owns the `lock` will return immediately if it invokes `lock` again.** The reentrant behavior of the lock allows recursively locking by the already owning thread, however the lock supports a maximum of `2147483647` lock by the same thread.

**Idiomatic Use of Lock**:

```
lock.lock();
try {
} finally {
lock.unlock();
}
```

**_Fairness_**: The `ReentrantLock` can be operated in _fair mode_ where the lock in granted to the longest waiting thread. Thus no thread experience starvation and the variance in times to obtain the lock is also small. Without the _fair mode_ the lock doesn't guarantee the order in which threads acquire the lock. When a lock is operated in fair mode in an environment with several threads contending access to the lock, throughput suffers and is significantly reduced. 

The method `tryLock` when invoked without timeout doesn't honor the fairness setting and acquires the lock if it is free even in the presence of other waiting threads.