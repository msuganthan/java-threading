**_ReentrantReadWriteLock_**: can be locked by **multiple readers at the same time** while writer threads have to wait. Conversely, the `ReentrantReadWriteLock` can be locked by a single writer thread at a time and other writer or reader have to wait for the lock to be free.

This allows threads to recursively acquire the lock. Internally, there are two locks to guard for read and write accesses.

This can help improve concurrency over using a mutual exclusion lock as it allows multiple reader thread to read concurrently. However, whether an application will truly realize concurreny improvements depends on other factors such as:
1. Running on multiprocessor machines
2. Frequency of reads and writes. Generally `ReadWriteLock` can improve concurrency in scenarios where read operations occur frequently and write operations are infrequent. If write operations happen often then most of the time is spent with the lock acting as a mutual exclusion lock.
3. Contention for data, i.e. the number of threads that try to read or write at the same time.
4. Duration of the read and write operations. If the read operations are very short then the overhead of locking `ReadWriteLock` versus a mutual exclusion lock can be higher.

In practice, you'll need to evaluate the access patterns to the shared data in your application to determine the suitability of using the `ReadWriteLock`.

**_Fair mode_**: The `ReentrantReadWriteLock` can also be operated in the _fair mode_, which grants entry to threads in a approximate arrival order. The longest waiting writer thread or a group of longest waiting reader threads is given preference to acquire the lock when it becomes free. In case of reader threads can acquire the lock concurrently.

**Cache example**: One common scenario where there can be multiple readers and writers is that of a cache. A cache is ususally used to speed up read requests from the another data source.