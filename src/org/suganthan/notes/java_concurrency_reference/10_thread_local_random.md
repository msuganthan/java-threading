<!-- TOC -->
  * [ThreadLocalRandom](#threadlocalrandom)
    * [Overview](#overview)
    * [Usage](#usage)
    * [Difference between Random and ThreadLocalRandom](#difference-between-random-and-threadlocalrandom)
<!-- TOC -->

## ThreadLocalRandom

### Overview

The class `java.util.concurrent.ThreadLocalRandom` is derived from the `java.util.Random` and generates random number much more efficiently than `java.util.Random` in multithreaded scenarios. Interestingly `Random` is thread-safe and can be used by multiple threads without malfunction, just not efficiently.

To understand why an instance of the `Random` class experiences overhead and contention in concurrent programs, we'll delve into the code for one of the most commonly used methods `nextInt()`

```java
protected int next(int bits) {
    long oldseed, nextseed;
    AtomicLong seed = this.seed;
    do {
        oldseed = seed.get();
        nextseed = (oldseed * multiplier + addend) & mask;
    } while(!seed.compareAndSet(oldseed, nextseed));
    return (int)(nextseed >>> (48 - bits));
}
```

The performance issues faced by `Random` are addressed by the `ThreadLocalRandom` class which is isolated in its effects to a single to a single thread. A random number generated by one thread using `ThreadLocalRandom` has no bearing on random numbers generated by other threads, unlike an instance of `Random` that generates random numbers globally.

Is a distinct `Random` object per thread is equivalent to using the `ThreadLocalRandom` class? The `ThreadLocalRandom` class is singleton and uses state held by the `Thread` class to generate random numbers.

### Usage

```java
import java.util.concurrent.ThreadLocalRandom;

class Demo {
    public static void main(String[] args) {
        System.out.println(ThreadLocalRandom.current().nextBoolean());

        System.out.println(ThreadLocalRandom.current().nextInt());

        System.out.println(ThreadLocalRandom.current().nextInt(500));

        System.out.println(ThreadLocalRandom.current().nextInt(700,1900));

        System.out.println(ThreadLocalRandom.current().nextDouble());

        System.out.println(ThreadLocalRandom.current().nextFloat());

        System.out.println(ThreadLocalRandom.current().nextGaussian());
    }
}
```

### Difference between Random and ThreadLocalRandom

```java
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class Demo {
    public static void main(String[] args) {
        performanceUsingRandom();
        performanceUsingThreadLocalRandom();
    }

    static void performanceUsingRandom() {
        Random random = new Random();
        ExecutorService executorService = Executors.newFixedThreadPool(15);

        Runnable task = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 50000; i++) {
                    random.nextInt();
                }
            }
        };

        int numThreads = 4;
        Future[] futures = new Future[numThreads];
        long start = System.currentTimeMillis();
        
        try {
            for (int i = 0; i < numThreads; i++) {
                futures[i] = executorService.submit(task);
            }

            for (int i = 0; i < numThreads; i++) {
                futures[i].get();
            }

            long executionTime = System.currentTimeMillis() - start;
            System.out.println("Execution time using Random : " + executionTime + " milliseconds");
        } finally {
            executorService.shutdown();
        }
    }

    static void performanceUsingThreadLocalRandom() throws Exception {

        ExecutorService es = Executors.newFixedThreadPool(15);

        Runnable task = new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < 50000; i++) {
                    ThreadLocalRandom.current().nextInt();
                }

            }
        };

        int numThreads = 4;
        Future[] futures = new Future[numThreads];
        long start = System.currentTimeMillis();

        try {
            for (int i = 0; i < numThreads; i++)
                futures[i] = es.submit(task);

            for (int i = 0; i < numThreads; i++)
                futures[i].get();

            long executionTime = System.currentTimeMillis() - start;
            System.out.println("Execution time using ThreadLocalRandom : " + executionTime + " milliseconds");

        } finally {
            es.shutdown();
        }
    }
}
```