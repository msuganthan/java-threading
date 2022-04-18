The lock interface provides a tool for implementing mutual exclusion that is more flexible and capable than `synchronized` methods and statements. A single thread is allowed to acquire a lock and gain access to a shared resource, however, some implementing classes such as the `ReentrantReadWriteLock` allow multiple threads concurrent access to shared resource.

The use of `synchronized` method or statement provides access to the implicit monitor lock associate with every object, **but requires all lock acquisitions and releases to proceed in a block-structured way.** Locks acquired in a nested fashion must be released in the exact opposite order, and all locks must be released in the same lexical scope in which they were acquired. **These requirement restrict how `synchronized` method and statements can be used and `Lock` implementations can be used for more complicated use-cases**

The `Lock` interface has the following classes implementing it:
1. ReentrantLock
2. ReentrantReadWriteLock.ReadLock
3. ReentrantReadWriteLock.WriteLock

**Difference between Lock and Synchronized**:

1. `Lock` can be tested for acquisition in a non-blocking fashion using `trylock()` method
2. `Lock` can be waited upon for acquisition with a specified timeout using the `trylock(timeout)` method. After the timeout the thread abandons its attempt to acquire the lock and moves-on.
3. `Lock` can be waited upon for acquisition with the option to interrupt the acquiring thread using the `lockInterruptibly` method.
4. Some `Lock` implementations also provide monitoring and deadlock detection. Additionally, `Lock` implementation can provide fair-use mode for locks, guranteed ordering and non-reentrant use.