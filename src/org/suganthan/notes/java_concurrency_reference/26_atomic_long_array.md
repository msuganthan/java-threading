## AtomicLong
### Overview

The class `AtomicLongArray` represents an array of type `long` that can be updated atomically. We can use long when the range of values we want to represent falls outside the range of values for an integer. An instance of the `AtomicLongArray` can be constructed either by passing an existing array of `long` or by specifying the desired size to the constructors of `AtomicLongArray`.

One notable difference between an ordinary array of `long`-s and an `AtomicLongArray` is that the latter provides volatile access semantics for its array elements, which isn’t supported for ordinary arrays.

### Example

```java
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

class Demonstration {

    public static void main( String args[] ) {
        long[] inputArray = new long[]{11, 17, 19, 23, 31};

        // use an array of ints to initialize an instance of AtomicIntegerArray
        AtomicLongArray atomicIntegerArray = new AtomicLongArray(inputArray);

        // index we'll manipulate
        int index = 3;

        // get operation
        long item = atomicIntegerArray.get(index);
        System.out.println("Item at index 3 = " + item);

        // set operation
        atomicIntegerArray.set(index, 37);
        System.out.println("Item now at index 3 = " + atomicIntegerArray.get(3));

        // compare and set operation.
        atomicIntegerArray.compareAndSet(index, 37, 41);
        System.out.println("Item at index 3 after compareAndSet operation = " + atomicIntegerArray.get(index));

        // addAndGet() - adds the passed-in argument and returns the result
        long result = atomicIntegerArray.addAndGet(index, 5);
        System.out.println("Item at index 3 after addAndGet(5) = " + result);

        // getAndAdd() - returns the value, and then adds the passed-in argument
        result = atomicIntegerArray.getAndAdd(index, 5);
        System.out.println("Item at index 3 after addAndGet(5) = " + result);

        // getAndIncrement() - gets the value and then increments
        result = atomicIntegerArray.getAndIncrement(index);
        System.out.println("Item at index 3 after getAndIncrement() = " + result);

        // incrementAndGet() - increments and then returns the result
        result = atomicIntegerArray.incrementAndGet(index);
        System.out.println("Item at index 3 after incrementAndGet() = " + result);

        // decrementAndGet() - decrements and then gets the result
        result = atomicIntegerArray.decrementAndGet(index);
        System.out.println("Item at index 3 after decrementAndGet() = " + result);

        // getAndDecrement() -
        result = atomicIntegerArray.getAndDecrement(index);
        System.out.println("Item at index 3 after getAndDecrement() = " + result);
    }
}
```
### Difference between AtomicLongArray and an array of AtomicLong-s

We can also create an array of `AtomicLong`-s instead of creating an `AtomicLongArray` but there are subtle differences between the two. These are: Creating an array of `AtomicLong`-s requires instantiating an instance of `AtomicLong` for every index of the array, whereas in case of `AtomicLongArray`, we only instantiate an object of the `AtomicLongArray` class. In other words, using an array of `AtomicLong`-s requires an object per element whereas `AtomicLongArray` requires an object of the class and an array object… Both classes provide for updating the long values present at the indexes atomically, however, in case of array of `AtomicLong`-s updating the object present at the index itself isn’t thread-safe. A thread can potentially overwrite the `AtomicLong` object at say index 0 with a new object. Such a situation isn’t possible with `AtomicLongArray` since the class only allows `long` values to be passed-in through the public methods for updating the long values the array holds. `AtomicLong []` is an array of thread-safe longs, whereas `AtomicLongArray` is a thread-safe array of longs.

Both classes are thread-safe when multiple threads update long values at various indexes.The following widget demonstrates ten threads randomly pick an index using `ThreadLocalRandom` and then add one to the long value at the chosen index of an instance of AtomicLongArray and an array of `AtomicLong`-s at the same index. At the end we should observe the same counts for all the indexes for both classes since the operations should be thread-safe.

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

class Demonstration {

    public static void main( String args[] ) throws Exception {

        final int arrayLength = 10;
        AtomicLongArray atomicLongArray = new AtomicLongArray(arrayLength);
        AtomicLong[] arrayOfAtomicLongs = new AtomicLong[arrayLength];

        for (int i = 0; i < arrayLength; i++) {
            arrayOfAtomicLongs[i] = new AtomicLong(0);
        }

        ExecutorService executor = Executors.newFixedThreadPool(15);

        try {

            for (int i = 0; i < arrayLength; i++) {

                executor.submit(new Runnable() {
                    @Override
                    public void run() {

                        for (int i = 0; i < 10000; i++) {
                            // choose a random index to add to
                            int index = ThreadLocalRandom.current().nextInt(arrayLength);

                            // add one to the integer at index i
                            atomicLongArray.addAndGet(index, 1);
                            arrayOfAtomicLongs[index].getAndAdd(1);
                        }
                    }
                });
            }

        } finally {
            executor.shutdown();
            executor.awaitTermination(1L, TimeUnit.HOURS);
        }

        // print the atomic integer array
        for (int i = 0; i < arrayLength; i++) {
            System.out.print(atomicLongArray.get(i) + " ");
        }

        System.out.println();

        // print the array of atomic integers
        for (int i = 0; i < arrayLength; i++) {
            System.out.print(arrayOfAtomicLongs[i].get() + " ");
        }
    }
}
```