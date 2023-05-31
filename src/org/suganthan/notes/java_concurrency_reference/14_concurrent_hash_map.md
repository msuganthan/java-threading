<!-- TOC -->
  * [ConcurrentHashMap](#concurrenthashmap)
    * [HashMaps and Concurrency](#hashmaps-and-concurrency)
    * [ConcurrentHashMaps](#concurrenthashmaps)
    * [Properties of ConcurrentHashMap](#properties-of-concurrenthashmap)
    * [Newbie Mistakes with ConcurrentHashMap](#newbie-mistakes-with-concurrenthashmap)
    * [Fixing with Atomic Integer](#fixing-with-atomic-integer)
    * [Fixing with Custom Counter Class#](#fixing-with-custom-counter-class)
    * [HashMap vs HashTable vs ConcurrentHashMap](#hashmap-vs-hashtable-vs-concurrenthashmap)
    * [Performance](#performance)
<!-- TOC -->

## ConcurrentHashMap

### HashMaps and Concurrency

HashMap is a commonly used data structure offering constant time access, however it is not thread-safe. Consider the two methods `get()` and `put()` that get invoked by two different threads on an instance of `HashMap` in the following sequence:

```java
1. Thread 1 invokes `put()` and inserts the key value pair ("myKey", "item-1")
2. Thread 2 invokes `get()` but before get operation completes, the thread is context-switched.
3. Thread 1 updates mykey with a new value say "item-2"
4. Thread 2 becomes active again but retrieves the stale key value pair ("myKey", "item-1")
```
### ConcurrentHashMaps

`ConcurrentHashMap` is a thread safe class and multiple threads can operate on it in parallel without incurring any of the issues that `HashMap` may suffer from in a concurrent environment. 

For write operations the entire map is never locked rather only a segment of the map is locked. However, the retrieval or read operation generally don't involve locking at all. **So in case of a read, the value set for a key by the most recently completed update operation is returned i.e. a completed update operation on a given key bears a happen before relationship with any read operation.** This does mean that a stale value may be returned if an update operation is in progress but not yet completed.

Another important details is that `Iterators`, `SplitIterator` or `Enumberations` for an instance of the `ConcurrentHashMap` represent the state or snapshot of the data structure at a point in time, specifically when they are created and don't throw the `ConcurrentModificationException` exception.

### Properties of ConcurrentHashMap

* `null` can't be inserted either as a key or a value.
* The `ConcurrentHashMap` shards its data into segments and the segments are locked individually when being written to. Each segment can be written independently of other segments allowing multiple threads to operate on the map object.
* The reads happen without locking for the majority of cases

### Newbie Mistakes with ConcurrentHashMap

One of the follies assumed when working with `ConcurrentHashMap` is to think that any accesses of and operation on the key/values within the data structure are somehow magically thread-safe. The map doesn't protect against external race conditions. Consider the below program that has two threads increment a key's value in a concurrentHashMap by a hundred times each.

```java
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class Demo {
    public static void main(String[] args) {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        map.put("Biden", 0);

        ExecutorService es = Executors.newFixedThreadPool(5);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    map.put("Biden", map.get("Biden") + 1);
                }
            }
        }

        Future future1 = es.submit(task);
        Future future2 = es.submit(task);
        
        future1.get();
        future2.get();
        
        es.shutdown();

        System.out.println("Votes for Biden == "+ map.get("Biden"));

    }
}
```

We sort of cheated in the above program to make it fail. Consider the line:

```java
map.put("Biden", map.get("Biden") + 1);
```

The above line is really three operations:

1. Retrieval of the value
2. incrementing the value
3. updating the value

The right implementation should execute all the three steps together as a transaction or atomically to avoid synchronization issues. The takeaway is that a `ConcurrentHashMap` doesn't protect its constituents from race conditions but access to the data structure itself is thread-safe.

### Fixing with Atomic Integer

One of the ways to fix the above program is to use instance of the `AtomicInteger`

```java
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

class Demo {
    public static void main(String[] args) {
        ConcurrentHashMap<String, AtomicInteger> map = new ConcurrentHashMap<>();
        AtomicInteger atomicInteger = new AtomicInteger(0);
        map.put("Biden", ai);

        ExecutorService es = Executors.newFixedThreadPool(5);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    map.get("Biden").incrementAndGet();
                }
            }
        };

        Future future1 = es.submit(task);
        Future future2 = es.submit(task);
        
        future1.get();
        future2.get();
        
        es.shutdown();

        System.out.println("Votes for Biden = "+map.get("Biden").get());
    }
}
```

The count of the above program will always be 200 no matter how many times you run it. **But this brings up another question: if we use AtomicInteger as value with the HashMap class would our program output the correct result?** The answer is yes for this naive/simple program because the atomic integer itself is thread-safe so multiple threads attempting to increment it do so serially.

However, the data structure i.e. the hash map itself is thread-unsafe and can exhibit concurrency bugs when multiple threads operate on it, traverse its keys or values, or when the map resizes. **Remember, we have to think about concurrency both at the map level and at the key/value level.**

### Fixing with Custom Counter Class#

We could re-write the above program with a class that tracks the count and perform explicit synchronization ourselves instead of relying on the AtomicInteger class. Read the listing below:

```java
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class Demonstration {
    
    // Class to keep track of vote count
    static class MyCounter {
        private int count = 0;

        void increment() {
            count++;
        }

        int getCount() {
            return count;
        }
    }

    public static void main( String args[] ) throws Exception {
        ConcurrentHashMap<String, MyCounter> map = new ConcurrentHashMap<>();
        map.put("Biden", new MyCounter());

        ExecutorService es = Executors.newFixedThreadPool(5);

        // create a task to increment the vote count
        Runnable task = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    MyCounter mc = map.get("Biden");
                    
                    // explicit synchronization
                    synchronized (mc) {
                        mc.increment();
                    }
                }
            }
        };

        // submit the task twice
        Future future1 = es.submit(task);
        Future future2 = es.submit(task);

        // wait for the threads to finish
        future1.get();
        future2.get();

        // shutdown the executor service
        es.shutdown();

        System.out.println("votes for Biden = " + map.get("Biden").getCount());
    }
}
```

### HashMap vs HashTable vs ConcurrentHashMap

| Property         | HashMap        | Hashtable           | Collection.synchronizedMap(...) | ConcurrentHashMap          |
|------------------|----------------|---------------------|---------------------------------|----------------------------|
| Null values/keys | yes            | no                  | depends on backing map          | no                         |
| Thread-safe      | no             | yes                 | yes                             | yes                        |
| Lock Mechanism   | not applicable | lock the entire map | locks the entire map            | locks a segment of the map |
| Iterator         | fail-fast      | fail-fast           | fail-fast                       | weakly consistent          |

### Performance

We can run a simple and unsophisticated test to observe the performance of the different types of maps. The program in the widget below has 5 threads writing the same keys to a map with an initial capacity of 10. We time the run for each of the maps we discussed. The output demonstrates ConcurrentHashMap outperforms the other two maps and has a higher write throughput. Note, that this test is crude and doesn’t take read performance into account but in general, the ConcurrentHashMap is the right choice in high concurrency environments.

```java
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class Demonstration {
    public static void main( String args[] ) throws Exception {

        // start executor service
        ExecutorService es = Executors.newFixedThreadPool(5);

        performanceTest(new Hashtable<>(10), "Hashtable", es);
        performanceTest(Collections.synchronizedMap(new HashMap<>(10)), "Collections.synchronized(HashMap)", es);
        performanceTest(new ConcurrentHashMap<>(10), "Concurrent Hash Map", es);

        // shutdown the executor service
        es.shutdown();

    }

    static void performanceTest(Map<String, Integer> map, String mapName, ExecutorService es) throws Exception {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000000; i++)
                    map.put("key-" + i, i);
            }
        };

        long start = System.currentTimeMillis();

        Future future1 = es.submit(task);
        Future future2 = es.submit(task);
        Future future3 = es.submit(task);
        Future future4 = es.submit(task);
        Future future5 = es.submit(task);

        // wait for the threads to finish
        future1.get();
        future2.get();
        future3.get();
        future4.get();
        future5.get();

        long end = System.currentTimeMillis();

        System.out.println("Milliseconds taken using " + mapName + ": " + (end - start));
    }    
}
```