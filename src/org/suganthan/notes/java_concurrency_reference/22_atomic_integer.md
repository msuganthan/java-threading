<!-- TOC -->
  * [AtomicInteger](#atomicinteger)
    * [Overview](#overview)
    * [Difference with int](#difference-with-int)
<!-- TOC -->

## AtomicInteger
### Overview

The AtomicInteger class represents an integer value that can be updated atomically, i.e. the read-modify-write operation can be executed atomically upon an instance of AtomicInteger. The class extends Number.

AtomicInteger makes for great counters as it uses the **compare-and-swap (CAS) instruction under the hood which doesn’t penalize threads competing for access to the same data with suspension as locks do**. In general, suspension and resumption of threads involves significant overhead and under low to moderate contention non-blocking algorithms that use CAS outperform lock-based alternatives.

### Difference with int

Remember that AtomicInteger isn’t equivalent to int. Specifically, AtomicInteger class doesn’t override equals() or hashcode() and each instance is distinct. AtomicInteger can’t be used as a drop-in replacement for an int or Integer.

```java
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

class Demonstration {

    public static void main( String args[] ) {
        // create map
        HashMap<AtomicInteger, String> mapAtomic = new HashMap<>();
        HashMap<Integer, String> mapInt = new HashMap<>();

        // create two instances with the same value 5
        AtomicInteger fiveAtomic = new AtomicInteger(5);
        AtomicInteger fiveAtomicToo = new AtomicInteger(5);

        // create two Integer instances
        Integer fiveInt = new Integer(5);
        Integer fiveIntToo = new Integer(5);

        // Though the key is 5, but the two AtomicInteger instances
        // have different hashcodes
        mapAtomic.put(fiveAtomic, "first five atomic");
        mapAtomic.put(fiveAtomicToo, "second five atomic");
        System.out.println("value for key 5 : " + mapAtomic.get(fiveAtomic));

        // With Integer type key, the second put overwrites the
        // key with Integer value 5.
        mapInt.put(fiveInt, "first five int");
        mapInt.put(fiveIntToo, "second five int");
        System.out.println("value for key 5 : " + mapInt.get(fiveInt));

    }
}
```