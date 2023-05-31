## AtomicIntegerFieldUpdater
### Overview

The field updater classes exist primarily for performance reasons. 
* Instead of using atomic variables, one can use ordinary variable that occasionally need to be `get` and then `set` atomically. 
* Another reason to avoid having atomic fields in objects that are **short-lived and frequently created** e.g. the next pointer of nodes in a concurrent linked list.

The atomicity guarantees for the updates classes are **weaker** than those of regular atomic classes because the underlying fields can still be modified directly i.e. without using the updater object. Additionaly, the atomicity 

### Example

As an example consider a Counter class that is very infrequently incremented or decremented but supports a very high number of read operations. For such a class, we may choose to track the count in an ordinary int variable instead of an AtomicInteger as we expect the Counter instance to be very infrequently updated. If such Counter objects are created in very large numbers then the cost savings in terms of space can be significant.

The code for the `Counter` class appears below along with comments.

```java
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

class Demo {
    public static void main(String[] args) {
        AtomicIntegerFieldUpdater<Counter> updater = AtomicIntegerFieldUpdater.newUpdater(Counter.class);
        Counter counter = new Counter();
        updater.compareAndSet(counter, 0, 1);

        System.out.println("Count = "+updater.get(counter));
    }

    static class Counter {
        protected volatile int count = 0;
    }
}
```

Note, that in the code widget above if we remove volatile with the int variable, the updater object will throw an error since only volatile fields can be updated using the atomic field updater classes.



