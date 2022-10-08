The Atomic* family of classes extend the notion of `volatile` variables that are designed to be operated upon without locking using machine-level atomic instructions available on modern processors.

Atomic* classes including `AtomicBoolean` offer a method `compareAndSet(expectedValue, updatedValue)` to conditionally update the value of the variable to `updatedValue` if it is set to `expectedValue` in one go, i.e. atomically. 

The read and write methods i.e. `get()` and `set()` on instances of this class are similar in behavior to volatile variables i.e `get()` has the memory effect of reading a volative variable and `set()` has the memory effect of writing a volatile variable.

**Difference between volatile and atomicClasses**: 

Apart from delivering a consistent view of the memory, the volatile keyword doesn't promise much in synchronization guarantees. Specifically, multiple threads accessing a `volatile` don't do so in a serialized manner. The onus of making a volatile variable's accesses synchronized and thread-safe is on the developer. This is where the `AtomicBoolean` classes come in. For instance, the methods `compareAndSet()` and `getAndSet()` exposed by the `AtomicBoolean` class represent a series of operations executed atomically, which otherwise would require synchronization on the part of the developer and are unachievable as an atomic transaction using `volatile` variables.

