<!-- TOC -->
  * [LongAccumulator](#longaccumulator)
    * [Overview](#overview)
      * [Distributing contention](#distributing-contention)
      * [Order of accumulation](#order-of-accumulation)
    * [Example](#example)
<!-- TOC -->

## LongAccumulator
### Overview

The `LongAccumulator` class is similar to the `LongAdder` class, except that the `LongAccumulator` class allows for a function to be supplied that contains the logic for computing results for accumulation. In contrast to `LongAdder`, we can perform a variety of mathematical operations rather than just addition. The supplied function to a `LongAccumulator` is of type `LongBinaryOperator`. The class `LongAccumulator` extends from the class `Number` but doesn’t define the methods `compareTo()`, `equals()`, or `hashCode()` and shouldn’t be used as keys in collections such as maps.

An example of creating an accumulator that simply adds long values presented to it

```java
// function that will be supplied to an instance of LongAccumulator
LongBinaryOperator longBinaryOperator = new LongBinaryOperator() {
   @Override
   public long applyAsLong(long left, long right) {
       return left + right;
   }
};

// instantiating an instance of LongAccumulator with an initial value of zero
LongAccumulator longAccumulator = new LongAccumulator(longBinaryOperator, 0);
```

Note that in the above example, we have supplied a function that simply adds the new long value presented to it. The method `applyAsLong` has two operands `left` and `right`. The `left` operand is the current value of the `LongAccumulator`. In the above example, it’ll be zero initially, because that is what we are passing-in to the constructor of the `LongAccumulator` instance. The code widget below runs this example and prints the operands and the final sum.

```java
import java.util.concurrent.atomic.LongAccumulator;
import java.util.function.LongBinaryOperator;

class Demonstration {
    public static void main( String args[] ) {
        // function that will be supplied to an instance of LongAccumulator
        LongBinaryOperator longBinaryOperator = new LongBinaryOperator() {
            @Override
            public long applyAsLong(long left, long right) {
                System.out.println(left + "  " + right);
                return left + right;
            }
        };

        // instantiating an instance of LongAccumulator with an initial value of zero
        LongAccumulator longAccumulator = new LongAccumulator(longBinaryOperator, 0);

        for (int i = 0; i < 10; i++) {
            longAccumulator.accumulate(1);
        }

        System.out.println("Final value = " + longAccumulator.get());
    }
}
```

#### Distributing contention

* We can achieve the same functionality by using an instance of `AtomicLong` as we can with the `LongAccumulator`, however, the rational for `LongAccumulator` is to distribute contention among threads by maintaining a set of variables that grow dynamically and each one is updated by only a subset of threads. Thus the contention is spread from a single variable to several variables. 

* When the current value is asked for by invoking the get() or the longValue() methods, all the underlying variables are accumulated by applying the supplied function and the result is returned. The expected throughput of LongAccumulator is significantly higher when used in place of AtomicLong under high contention. The improved performance comes at the cost of using more space.

#### Order of accumulation

* When multiple threads accumulate an instance of LongAccumulator, eventually all the long values in the underlying set are accumulated using the supplied function. The order in which these long values are accumulated isn’t guaranteed and the supplied function should produce the same value irrespective of the order in which these values are accumulated.
* In case, the supplied function isn’t commutative i.e., left + right isn’t the same as right + left then the accumulation can produce different results for the same series of accumulated long values.

### Example

In the example below, we use the `LongAccumulator` class to keep track of the maximum value observed. There are several threads that use the `ThreadLocalRandom` class to produce a random long value less than 1000, and then attempt to update the instance of `LongAccumulator`. We conduct the same test using `AtomicLong` and time the two tests. Go through the listing which is self-explanatory.

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.function.LongBinaryOperator;

class Demonstration {

    static int numThreads = 2;
    static int poolSize = 50;
    static int iterations = 10000;

    public static void main( String args[] ) throws Exception {
        testWithLongAccumulator();
        testWithAtomicLong();        
    }

    static void testWithLongAccumulator() throws Exception {

        // function that will be supplied to an instance of LongAccumulator
        LongBinaryOperator longBinaryOperator = new LongBinaryOperator() {
            @Override
            public long applyAsLong(long left, long right) {
                return left > right ? left : right;
            }
        };

        // instantiating an instance of LongAccumulator with the lowest possible min value
        LongAccumulator longAccumulator = new LongAccumulator(longBinaryOperator, Long.MIN_VALUE);

        ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
        long start = System.currentTimeMillis();

        try {
            for (int i = 0; i < numThreads; i++) {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        for (int j = 0; j < iterations; j++) {
                            long value = ThreadLocalRandom.current().nextLong(1000);
                            longAccumulator.accumulate(value);
                        }
                    }
                });
            }
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);
        }

        long timeTaken = System.currentTimeMillis() - start;
        System.out.println("Time taken by LongAccumulator " + timeTaken + " milliseconds and max value observed = " + longAccumulator.get());
    }

    static void testWithAtomicLong() throws Exception {

        int numThreads = 20;

        AtomicLong atomicLong = new AtomicLong(Long.MIN_VALUE);

        ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
        long start = System.currentTimeMillis();

        try {
            for (int i = 0; i < numThreads; i++) {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        for (int j = 0; j < iterations; j++) {
                            long value = ThreadLocalRandom.current().nextLong(1000);

                            long currentMax;

                            do {
                                currentMax = atomicLong.get();
                                if (currentMax > value)
                                    break;
                            } while (!atomicLong.compareAndSet(currentMax, value));
                        }
                    }
                });
            }
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);
        }

        long timeTaken = System.currentTimeMillis() - start;
        System.out.println("Time taken by AtomicLong " + timeTaken + " milliseconds and max value observed = " + atomicLong.get());
    }
}
```