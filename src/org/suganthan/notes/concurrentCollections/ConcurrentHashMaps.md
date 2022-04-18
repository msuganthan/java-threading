**_CurrentHashMap_**: is a thread-safe class and multiple threads can operate on it in parallel with incurring any of the issues that a `HashMap` may suffer from in a concurrent environment. For `write` operations the entire map is never locked rather only a segment of the map is locked. However, the retrieval or read operations generally don't involve locking at all. So in case of a read, the value set for a key by the most recently completed operation is returned i.e. a completed update operation on a given key bears a happens before relationship with any read operation. This does mean that a stale value map be returned if an update operation is in progress but not yet completed.

Since read operations can happen while update operations are on-going, any concurrent reads during the execution of aggregate operations such as `putAll()` and `clear()` may return insertion or removal of some of the entries respectively, when all or none are expected.

Another important detail is that `Iterator`, `SplitIterator` or `Enumerations` for an instance of the `ConcurrentHashMap` represent the state or snapshot of the data structure at a point in time, specifically when they are created and don't throw the `ConcurrentModificationException` exception.

**_Properties_**: 
1. Null can't be inserted either as a key or a value.
2. The `ConcurrentHashMap` shards its data into segments and the segments are locked individually when being written to. Each segment can be written independently of other segments allowing multiple threads to operate on the map object.
3. The reads happen without locking for the majority of cases, thus making them synchronization-free and improving performance. However, note that there are certain minority scenario when reads have to go through synchronization.
4. In general, using keys that evaluate to the same hashcode will slow down the performance of any hash map.

**_Mistakes with ConcurrentHashMap_**: The map doesn't protect against external race conditions.  