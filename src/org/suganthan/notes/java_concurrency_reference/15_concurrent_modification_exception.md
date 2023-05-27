## Concurrent Modification Exception

### Single Threaded Environment

The name may sound related to concurrency, however the exception can be thrown while a single thread operates on a map. The exception occurs when a map is modified at the same time any of its collection views is being traversed.

```java
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class Demo {
    public static void main(String[] args) {
        HashMap<String, Integer> map = new HashMap<>();
        int i = 0;
        for (int i = 0; i < 100; i++) {
            map.put("key-" + i, i);
        }

        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            map.put("key-"+i, i);
            iterator.next();
            i++;
        }
    }
}
```

### Multi Threaded Environment

In case of a single threaded environment it is often trivial to diagnose `ConcurrentModifictionException` cause, however, in multithreaded scenarios, it may be difficult to do so as the exception may occur intermittently depending on how threads are scheduled for execution. Concurrent modification occurs when one thread is iterating over a map while another thread attempts to modify the map at the same time. A usual sequence of events is as follows:

1. Thread A obtains an iterator for the keys, values or entry set of a map.
2. Thread A begins to iterate in a loop.
3. Thread B comes along and attempts to delete, insert or update a key/value pair in a map.
4. `ConcurrentModifictionException` is thrown when thread A attempts to retrieve the next item in the collection it is iterating.

Since the map has been modified from the time the iterator for the map was created, the thread iterating over the collection can observe inconsistent data and a `ConcurrentModificationException` is thrown. The program below demonstrates interaction between two threads that results in the exception.

```java
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class Demo {
    public static void main(String[] args) {
        HashMap<String, Integer> map = new HashMap<>();
        ExecutorService es = Executors.newFixedThreadPool(5);

        try {
            Runnable reader = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        //ignore
                    }

                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ie) {
                            //ignore
                        }
                        System.out.println("Key " + entry.getKey() + " value " + entry.getValue());
                    }
                }
            };

            Runnable writer = new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 100; i++) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ie) {
                            //ignore
                        }
                        map.put("Key-" + i, i);
                    }
                }
            };

            Future future1 = es.submit(writer);
            Future future2 = es.submit(reader);
            
            future1.get();
            future2.get();
        } finally {
            es.shutdown();
        }
    }
}
```

It is not only the HashMap that suffers from ConcurrentModificationException, other maps exhibit same behavior. The only map that is designed to be concurrently modified while being traversed is the ConcurrentHashMap. The program below demonstrates the behavior of all the maps.

```java
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class Demonstration {
    public static void main( String args[] ) {
        test(new Hashtable<String, Integer>());
        test(new HashMap<String, Integer>());
        test(Collections.synchronizedMap(new HashMap<String, Integer>()));
        test(new ConcurrentHashMap<String, Integer>());
    }

    static void test(Map<String, Integer> map) {

        // Put some data in the map
        int i;
        for (i = 0; i < 10; i++) {
            map.put("key-" + i, i);
        }

        Iterator it = map.entrySet().iterator();

        while (it.hasNext()) {
            map.put("key-" + i, i);
            try {
                it.next();
            } catch (ConcurrentModificationException ex) {
                System.out.println("ConcurrentModificationException thrown for map " + map.getClass().getName());
                return;
            }
            i++;
        }

        System.out.println("No exception thrown for map " + map.getClass().getName());
    }    
}
```

Even though the ConcurrentHashaMap can undergo concurrent modifications (additions, deletions, updates) at the same time as its elements are being traversed, the modifications may not be reflected during the traversal.

```java
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class Demo {
    public static void main(String[] args) {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        try {
            Runnable reader = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ie) {
                        //ignore
                    }
                    int seen = 0;
                    for (Map.Entry<String, Integer> entry : map.entrySet()) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ie) {
                            //ignore
                        }
                        entry.getValue();
                        seen++;
                    }
                    System.out.println("Number of entries seen by the reader thread : " + seen);
                }
            };

            Runnable writer = new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 1000; i++) {
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException ie) {
                            //ignore
                        }
                        map.put("key-" + i, i);
                    }
                    System.out.println("Writer thread finished...");
                }
            };

            Future future1 = executorService.submit(writer);
            Future future2 = executorService.submit(reader);
            
            future1.get();
            future2.get();
        } finally {
            executorService.shutdown();
        }
    }
}
```
In the above program, the writer inserts 1000 entries into the map but the reader only sees a handful.

As a user of `ConcurrentHashMap` one has to be cognizant of the limitation of iterator/enumerator, while may return a snapshot of the map taken at the time of creation of the iterator/enumeration or later.

```java
static void quiz() {

    Map<String, Integer> map = new HashMap<>();
    Random random = new Random(System.currentTimeMillis());

    // Put some data in the map
    for (int i = 0; i < 10; i++) {
        map.put("key-" + i, i);
    }

    Iterator it = map.entrySet().iterator();

    while (it.hasNext()) {
        it.next();
        int k = random.nextInt(10);
        map.put("key-" + k, k);
    }
}
```

The above scenario is very interesting since we are modifying the map but we are essentially overwriting the same key/value pair and no exception is thrown. In some cases such as handling duplicates (e.g. key/value pairs received from a message bus), a program can overwrite the same key/value pair twice (an example of idempotent write) and continue to function correctly but under different conditions may throw ConcurrentModificationException.

```java
import java.util.*;

class Demonstration {
    public static void main( String args[] ) {
        Map<String, Integer> map = new HashMap<>();
        Random random = new Random(System.currentTimeMillis());

        // Put some data in the map
        for (int i = 0; i < 10; i++) {
            map.put("key-" + i, i);
        }

        Iterator it = map.entrySet().iterator();

        while (it.hasNext()) {
            it.next();
            int k = random.nextInt(10);
            map.put("key-" + k, k);
        } 

        System.out.println("Program completes successfully.");    
    }
}
```