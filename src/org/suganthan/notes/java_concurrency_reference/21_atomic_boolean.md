<!-- TOC -->
  * [AtomicBoolean](#atomicboolean)
    * [Explanation](#explanation)
    * [Difference between Volatile boolean and AtomicBoolean](#difference-between-volatile-boolean-and-atomicboolean)
<!-- TOC -->

## AtomicBoolean
### Explanation

The `Atomic*` family of classes extend the notion of `volatile` variables that are designed to be operated upon without locking using machine-level atomic instructions available on modern processors.

`Atomic*` classes including `AtomicBoolean` offer a method `compareAndSet(expectedValue, updatedValue)` to conditionally update the value of the variable to `updatedValue` if it is set to `expectedValue` in one go, i.e. atomically. All read-and-update methods except for `lazySet()` and `weakCompareAndSet()` have memory effects equivalent of both reading and writing `volatile` variables.

The read and write methods i.e. `get()` and `set()` on instances of this class are similar in behavior to volatile variables i.e `get()` has the **memory effect** of reading a volatile variable and `set()` has the **memory effect** of writing a volatile variable.

### Difference between Volatile boolean and AtomicBoolean

Apart from delivering a consistent view of the memory, **the volatile keyword doesn't promise much in synchronization guarantees**. Specifically, multiple threads accessing a `volatile` don't do so in a serialized manner. **The onus of making a volatile variable's accesses synchronized and thread-safe is on the developer.** This is where the `AtomicBoolean` classes come in. For instance, the methods `compareAndSet()` and `getAndSet()` exposed by the `AtomicBoolean` class **represent a series of operations executed atomically**, which otherwise would require synchronization on the part of the developer and are unachievable as an atomic transaction using `volatile` variables.

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class Demo {
    static volatile boolean won = false;

    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(25);
        try {
            int numThreads = 15;
            Runnable[] racers = new Runnable[numThreads];
            Future[] futures = new Future[numThreads];

            for (int i = 0; i < numThreads; i++) {
                racers[i] = new Runnable() {
                    @Override
                    public void run() {
                        race();
                    }
                };
            }

            for (int i = 0; i < numThreads; i++) {
                futures[i] = service.submit(racers[i]);
            }

            for (int i = 0; i < numThreads; i++) {
                futures[i].get();
            }
        } finally {
            service.shutdown();
        }
    }
    
    static void race() {
        if (!won) {
            won = true;
            System.out.println(Thread.currentThread().getName() + " won the race.");
        } else {
            System.out.println(Thread.currentThread().getName() + " lost.");
        }
    }
}
```

If you run the above program enough times especially on a machine with multiple processes you’ll observe multiple threads printing the winning statement. A possible sequence resulting in multiple threads declaring themselves as winners can be:

1. Thread A reads the value of won which is false.
2. Thread B reads the value of won which is false.
3. Thread A changes the value of won to true and the variable is updated in main memory for all threads to see.
4. Thread B doesn’t know Thread A had read the value of the variable won before Thread B accessed it. Thread B too updates the value of won to true.
5. Since won is a volatile variable, it always reflects its latest value to the next thread reading it but that’s about it.

The above program can be fixed by using `AtomicBoolean`

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

class Demonstration {

    static AtomicBoolean won = new AtomicBoolean(false);

    public static void main( String args[] ) throws Exception {
        ExecutorService es = Executors.newFixedThreadPool(25);
        try {
            int numThreads = 15;
            Runnable[] racers = new Runnable[numThreads];
            Future[] futures = new Future[numThreads];

            // create thread tasks
            for (int i = 0; i < numThreads; i++) {
                racers[i] = new Runnable() {
                    @Override
                    public void run() {
                        race();
                    }
                };
            }

            // submit threads for execution
            for (int i = 0; i < numThreads; i++) {
                futures[i] = es.submit(racers[i]);
            }

            // wait for threads to finish
            for (int i = 0; i < numThreads; i++) {
                futures[i].get();
            }
        } finally {
            es.shutdown();
        }        
    }

    static void race() {
        if (won.compareAndSet(false, true)) {
            System.out.println(Thread.currentThread().getName() + " won the race.");
        } else {
            System.out.println(Thread.currentThread().getName() + " lost.");
        }
    }

}
```

