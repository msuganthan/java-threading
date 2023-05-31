<!-- TOC -->
  * [AtomicIntegerArray](#atomicintegerarray)
    * [Overview](#overview)
    * [Example](#example)
    * [Difference between AtomicIntegerArray and an array of AtomicIntegers](#difference-between-atomicintegerarray-and-an-array-of-atomicintegers)
<!-- TOC -->

## AtomicIntegerArray
### Overview

The class `AtomicIntegerArray` represents an array of type `int` that can be updated atomically.

One notable difference between an ordinary array of `int-s` and an `AtomicIntegerArray` is that the latter provides volatile access semantics for its array element, which isn't supported for ordinary array.

### Example

```java
import java.util.concurrent.atomic.*;
import java.util.concurrent.*;

class Demonstration {
    public static void main( String args[] ) {

        int[] inputArray = new int[]{11, 17, 19, 23, 31};

        // use an array of ints to initialize an instance of AtomicIntegerArray
        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(inputArray);

        // index we'll manipulate
        int index = 3;

        // get operation
        int item = atomicIntegerArray.get(index);
        System.out.println("Item at index 3 = " + item);

        // set operation
        atomicIntegerArray.set(index, 37);
        System.out.println("Item now at index 3 = " + atomicIntegerArray.get(3));

        // compare and set operation.
        atomicIntegerArray.compareAndSet(index, 37, 41);
        System.out.println("Item at index 3 after compareAndSet operation = " + atomicIntegerArray.get(index));

        // addAndGet() - adds the passed-in argument and returns the result
        int result = atomicIntegerArray.addAndGet(index, 5);
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

### Difference between AtomicIntegerArray and an array of AtomicIntegers

We can also create an array of `AtomicIntegers` instead of creating an `AtomicIntegerArray` but there are subtle difference between the two. These are:

* Creating an array of `AtomicInteger's` requires instantiating an instance of `AtomicInteger` for every index of the array, whereas in case of `AtomicIntegerArray`, we only instantiate an object of the `AtomicIntegerArray` class.
* Both classes provide for updating an integer values present at the indexes atomically, however, in case of array of `AtomicInteger` updating the object present at the index isn't thread-safe. A thread can potentially overwrite the `AtomicInteger` object at say index `0` with a new object. Such a situation isn't possible with `AtomicIntegerArray` since the class only allows `int` values to be passed-in through the public methods for updating the integer values the array holds.
* `AtomicInteger[]` is an array of thread-safe integers, whereas `AtomicIntegerArray` is a thread-safe array of integers.
* Both classes are thread-safe when multiple threads update integer values at various indexes.

```java
import java.util.concurrent.atomic.*;
import java.util.concurrent.*;

class Demonstration {

    public static void main( String args[] ) throws Exception {
        final int arrayLength = 10;
        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(arrayLength);
        AtomicInteger[] arrayOfAtomicIntegers = new AtomicInteger[arrayLength];

        for (int i = 0; i < arrayLength; i++) {
            arrayOfAtomicIntegers[i] = new AtomicInteger(0);
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
                            atomicIntegerArray.addAndGet(index, 1);
                            arrayOfAtomicIntegers[index].getAndAdd(1);
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
            System.out.print(atomicIntegerArray.get(i) + " ");
        }

        System.out.println();

        // print the array of atomic integers
        for (int i = 0; i < arrayLength; i++) {
            System.out.print(arrayOfAtomicIntegers[i].get() + " ");
        }
    }
}
```