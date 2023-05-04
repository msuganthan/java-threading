### Cons of Locking

**Thread scheduling vs useful work**

**JVM is very efficient when it comes to acquiring and releasing a lock that is requested by a single thread.** However, when **multiple threads attempt to acquire the same lock**, only one wins and rest must be suspended. The suspension and resumption of threads is costly and introduces significant overhead and this can be an issue for scenarios where several threads content for the same lock but execute very little functionality. IN such cases, the time spent in scheduling overhead overwhelms the useful work done. This is true of synchronized collections where the majority of methods perform very few operations.

**Priority Inversion**

A higher priority thread can be blocked by a lower priority thread that holds the lock and itself is blocked because of a page fault, scheduling delay etc. This situation effectively downgrades the priority of the higher priority thread to that of the lower-priority thread since the former can't make progress until the latter releases the lock. In general, all threads that require a particular lock can't make progress until the thread holding the lock releases it.

**Liveness issues**

Locking also introduces the possibility of liveness issues such as **deadlocks, livelock or simply programming bugs that have threads caught in infinite loops blocking other threads from making progress**.

**Locking, a heavyweight mechanism**

In general locking is a heavyweight mechanism, specially for fine-grained tasks such as manipulating a cuter. Locking is akin to assuming the worst or preparing for the worst possible scenario, i.e. the thread assumes it would necessarily run into contention with another thread and acquires a lock to manipulate shared state. Another approach could be to update stared state hoping it would complete without contention/interference from other participants. **In case contention is detected, the update operation can be failed and if desired reattempted later**. We'll see how this approach is supported by hardware later.

**Atomic vs Volatile**

Short of locking, we have volatile variables that promise the same visibility guarantees as a lock, however, volatile variables can't be used for:

* Executing compound actions, e.g. Decrementing a counter involves fetching the counter value, decrementing it and then writing the updated value for a total of three steps.
* When the value of a variable depends on another or the new value of a variable depends on its older value.

The above limitations are addressed by atomic classes, which offer similar memory visibility guarentess as volatile variables and also allow operations such as read-modify-write to be executed atomically, As an aside consider the follow snippet:

`volatile AtomicInteger atomicInteger = new AtomicInteger()`

Note that the marking the `AtomicInteger` variable above `volatile` isn't superfluous and implies that when the variable `atomicInteger` is updated to a new reference, **the update value `atomicInteger` hold will be observed by all threads that read the variable**. **In the absence of `volatile` the `atomicInteger` variable's value may get cached by a processor and the new object the variable points to after the update may not be visible to the processor that cached the old value**.

Atomic variables can also be thought of as `better volatiles`. Atomic variables make ideal **counters, sequence generators and variables that accumulate running statistics**. They offer the **same memory semantics as volatile variables with additional support of atomic updates and may be better choices than volatile variables in most circumstances**.

**Atomic Processor Instructions**

Modern processors have instructions that can atomically execute compound operations offering a compromise between locking and volatile variables. Hardware support for concurrency is ubiquitous in present day processors and the most well-known of these instruction is the **_Compare and Swap_** instruction or CAS for short. The CAS instruction is the secret sauce behind atomically executing compound operations.

**Compare and Swap**

In general, the CAS instruction has three operands:

1. A memory location, say M, representing the variable we desire to manipulate
2. An expected value for the variable, say A. This is the latest value seens for the variable.
3. The new value, say B which we want the variable to update to

CAS instruction works by performing the following action atomically,

1. Check the latest value of the memory location M
2. If the memory location has a value other than A, then it implies that another thread change the variable since the last time we examined it and therefore the requested updated **operation should be aborted**.
3. If the variable's value is indeed A, then it implies that no other thread has had a chance to change the variable to a different value than A, since we last examined the variable's value, and therefore we can proceed to update the variable/memory location to the new value B.

CAS takes the optimistic approach when performing an update on a shared variable. Expressed in prose, the instruction says _I have seen and expect the variable’s value to be A, if that is still true, update the variable to the new value B, otherwise fail my request and let me know. When multiple threads invoke CAS to manipulate a shared variable, only a single thread succeeds and the rest fail_. However, the crucial difference between CAS and locking is that **with CAS the threads that fail executing the CAS command have a choice to retry or go do some other useful work, unlike locking where all the threads unsuccessful in acquiring the lock will block**. This may sound trivial but can improve throughput and performance many fold.


The idiomatic usage of CAS usually takes the form of reading the value A of a shared variable, deriving a new value B from the value A, and finally invoking CAS to update the variable from A to B if it hasn’t been changed to another value in the meantime.

**ABA Problem**

1. A thread T1 reads the value of a shared variable as A and desires to change it to B. After reading the variable’s value, thread

2. T1 undergoes a context switch. Another thread, T2 comes along, changes the value of the shared variable from A to B and then back to A from B.

3. Thread T1 is scheduled again for execution and invokes CAS with A as the expected value and B as the new value. CAS succeeds since the current value of the variable is A, even though it changed to B and then back to A in the time thread T1 was context switched.

For some algorithms the ABA problem may not be an issue but for others changing the value from A to B and then back to A may require re-executing some step(s) of an algorithm. **This problem usually occurs when a program manages its own memory rather than leaving it to the Garbage Collector**. For example, **you may want to recycle the nodes in your linked list for performance reasons**. Thus noting that the head of the **list still references the same node, may not necessarily imply that the list wasn’t changed**. One solution to this problem is to **attach a version number with the value**, i.e. instead of storing the value as A, we store it as a pair (A, V1). Another thread can change the value to (B, V1) but when it changes it back to A the associated version is different i.e. (A, V2). In this way, a collision can be detected. There are two classes in Java that can be used to address the ABA problem:

1. `AtomicStampedReference`
2. `AtomicMarkableReference`

**Taxonomy of atomic classes**

There are a total of sixteen atomic classed divided into four groups:

1. Scalars
2. Field Updaters
3. Arrays
4. Compound variables

Most well-known and commonly used are the scalar ones such as `AtomicInteger`, `AtomicLong` `AtomicReference`, which support the CAS(compare-and-set). **Other primitive types such as `double` and `float` can be simulated by casting `short` and `byte` values to and from `int` and using method `floatToIntBits()` and `doubleToLongBits()` for floating point numbers**. **Atomic scalar classes extend from `Number` and don't redefine `hashCode()` or `equals()`**

**Atomics are not primitives**

The following widget highlights these differences between `Integer` and `AtomicInteger`. Note, that the `Integer` class has the same hashcode for the same integer value but that's not the case for `AtomicInteger`. **Thus `Atomic*` scalar classes are unsuitable as keys for collections that rely on hashcode.**

```java
import java.util.concurrent.atomic.AtomicInteger;

class Demo {
    public static void main(String[] args) {
        AtomicInteger atomicFive = new AtomicInteger(5);
        AtomicInteger atomicAlsoFive = new AtomicInteger(5);

        System.out.println("atomicFive.equals(atomicAlsoFive) => "+atomicFive.equals(atomicAlsoFive));
        System.out.println("atomicFive.hashCode() == atomicAlsoFive.hashCode() => "+(atomicFive.hashCode() == atomicAlsoFive.hashCode()));

        Integer integer1 = 23235;
        Integer integer2 = 23235;

        System.out.println("integer1.equals(integer2) : " + integer1.equals(integer2));
        System.out.println("integer1.hashCode() == integer2.hashCode() : " + (integer1.hashCode() == integer2.hashCode()));
    }
}
```

Atomics provides the users with an option to back off when face with contention. For instance the `AtomicInteger` class has a `compareAndSet()` method that returns false if the operation doesn't succeed. **The invoking thread now has the opportunity to either retry the operation immediately or use a custom retry strategy**. In essence, **responding to contention or _contention management_ is pushed to the invoking thread and the JVM doesn't make a decision for the caller, as it does in case of locking by suspending the thread**.

### Performance of atomics vs Locks

In the case of a single thread, i.e. zero contention environment, an operation that relies on CAS will be faster than an operation that involves locking first. On single CPU machines, CAS operations almost always succeed other than in the very rare case of a thread being interrupted in the middle of a read-modify-write operation. **Even with moderate contention, CAS operations are faster as they avoid thread suspension and resumption, which locking solutions must deal with.**

In benchmark tests, it has been observed that atomics perform better than locks under **low to moderate contention**, which is representative of real-life programs. However, in a highly contended environment, the majority of threads will waste CPU cycles retrying CAS oerations but using locking the same situation would have the threads suspended and then resumed later. Granted, thread suspension/resumption incurs overhead but at some point the CAS-retry expense dwarfs the overhead of suspending and resuming threads. **In reality such high contention is unlikely and atomics are always preferable over lock-based solutions.**

We can conduct a crude test to measure the performance of an AtomicInteger counter versus an ordinary counter. The test involves creating a thread pool of ten threads and then having each thread increment the same counter a million times so that the counter value reaches ten million at the end of the test. We time the two scenarios in the widget below:

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

class Demo {
    static AtomicInteger counter = new AtomicInteger(0);
    static in simpleCounter = 0;

    public static void main(String[] args) throws Exception {
        test(true);
        test(false);
    }

    synchronized static void incrementSimpleCounter() {
        simpleCounter++;
    }

    static void test(boolean isAtomic) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        long start = System.currentTimeMillis();
        
        long timeTaken = System.currentTimeMillis() - start;
        System.out.println("Time taken by " + (isAtomic ? "atomic integer counter " : "integer counter ") + timeTaken + " milliseconds.");
    }
}
```

### Keeping state thread local

Finally, the best choice for scalability and performance **is to share as little state as possible amount threads**. Keeping variables and state, thread local results in maximum performance and elimination of contention. Even though atomics achieve better scalability than using locks, choosing not to share any state amoung threads will result in the best scalability.