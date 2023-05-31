<!-- TOC -->
  * [AtomicLong](#atomiclong)
    * [Overview](#overview)
    * [Performance](#performance)
    * [Difference with long](#difference-with-long)
    * [Using AtomicLong to simulate atomic double](#using-atomiclong-to-simulate-atomic-double)
<!-- TOC -->

## AtomicLong
### Overview

`AtomicLong` is the equivalent class for `long` type in the `java.util.concurrent.atomic` package as is `AtomicInteger` for `int` type. The `AtomicLong` class represents a long value that can be updated atomically, i.e. the read-modify-write operation can be executed atomically upon an instance of `AtomicLong`. The class extends `Number`.

### Performance

To demonstrate the performance of `AtomicLong` we can construct a crude test, where a counter is incremented a million times by ten threads to reach a total of ten million. We’ll time the run for an `AtomicLong` counter and an ordinary `long` counter. The widget below outputs the results:

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.*;

class Demonstration {

    static long simpleCounter;
    static AtomicLong atomicCounter;

    public static void main( String args[] ) throws Exception {
        test(true);
        test(false);
    }

    synchronized static void incrementSimpleCounter() {
        simpleCounter++;
    }

    static void test(boolean isAtomic) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        long start = System.currentTimeMillis();

        try {
            for (int i = 0; i < 10; i++) {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 1000000; i++) {

                            if (isAtomic) {
                                atomicCounter.incrementAndGet();
                            } else {
                                incrementSimpleCounter();
                            }
                        }
                    }
                });
            }
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);
        }

        long timeTaken = System.currentTimeMillis() - start;
        System.out.println("Time taken by " + (isAtomic ? "atomic long counter " : "long counter ") + timeTaken + " milliseconds.");
    }    
}
```

### Difference with long

Remember that `AtomicLong` isn’t a drop-in replacement for `long`. Specifically, just like the `AtomicInteger` class, the `AtomicLong` doesn’t override `equals()` or `hashcode()` and each instance is distinct. The widget below demonstrates that the same long value for two different `AtomicLong` instances doesn’t hash to the same bucket.

```java
import java.util.concurrent.atomic.*;
import java.util.HashMap;

class Demonstration {
  
    public static void main( String args[] ) {
        // create map
        HashMap<AtomicLong, String> mapAtomic = new HashMap<>();
        HashMap<Long, String> mapLong = new HashMap<>();

        // create two instances with the same long value 5
        AtomicLong fiveAtomic = new AtomicLong(5);
        AtomicLong fiveAtomicToo = new AtomicLong(5);

        // create two long instances
        Long fiveLong = new Long(5);
        Long fiveLongToo = new Long(5);

        // Though the key is 5, but the two AtomicInteger instances
        // have different hashcodes
        mapAtomic.put(fiveAtomic, "first five atomic");
        mapAtomic.put(fiveAtomicToo, "second five atomic");
        System.out.println("value for key 5 : " + mapAtomic.get(fiveAtomic));

        // With Integer type key, the second put overwrites the
        // key with Integer value 5.
        mapLong.put(fiveLong, "first five long");
        mapLong.put(fiveLongToo, "second five long");
        System.out.println("value for key 5 : " + mapLong.get(fiveLong));
    }
}
```

### Using AtomicLong to simulate atomic double

*