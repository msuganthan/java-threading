**ThreadPoolExecutor**:
A thread pool is a group of threads instantiated and kept alive to execute submitted tasks. Thread pools can
achieve better performance and throughput than creating an individual thread per task by circumventing the
the overhead associated with thread creation and destruction. Additionally, system resources can be better
managed using a thread pool, which allows us to limit the number of threads in the system.

**Executors factory methods**:

    1. newCachedThreadPool
    2. newFixedThreadPool
    3. newSingleThreadExecutor
    4. newScheduledThreadPool

**Constructor**:

    public ThreadPoolExecutor(int corePoolSize,
                             int maximumPoolSize,
                             long keepAliveTime,
                             TimeUnit unit,
                             BlockingQueue<Runnable> workQueue,
                             RejectedExecutionHandler handler)

    new ThreadPoolExecutor(corePoolSize: 1,
            maximumPoolSize: 5,
            keepAliveTime: 1,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(3),
            new ThreadPoolExecutor.AbortPolicy());

The arguments `corePoolSize` and `maximumPoolSize` together determines the number of threads that get created in the pool.

* When the pool has less than `corePoolSize` threads and a new task arrives, new thread is instantiated even if the other threads in the pool are idle.
* When the pool has more than `corePoolSize` threads but less then `maximumPoolSize` threads then a new thread is only created if the queue that holds the submitted task is full.
* The maximum number of threads that can be created is capped by `maximumPoolSize`.

`CorePoolSize` == `maximumPoolSize`: we are effectively creating a fixed size thread pool.

`maximumPoolSize` to an arbitrary high value: unbounded value such as Integer.MAX_VALUE allows the pool to accommodate an arbitrary number of concurrent tasks.

`Keep-alive`: The thread pool will eliminate threads in excess of `corePoolSize` after `keepAliveTime` has elapsed.

`ThreadFactory`: The pool creates new threads using a ThreadFactory. The user has the choice to pass-in a factory of her own choice or let the `ThreadPoolExecutor` class choose the default.

**Queuing**: The queue works in tandem with the pool's thread size.
    1. If fewer than `corePoolSize` threads are running when a new task is submitted, the executor prefers adding a new thread rather than queueing a task.
    2. If `corePoolSize` or more threads are running, the executor prefers queuing the task than creating a new thread.
    3. If the queue is full and creating a new thread would exceed `maximumPoolSize` the submitted task is rejected by the executor.

**Queuing Strategies**:

*Direct handoffs*:
1.Direct handoff design involves an object running in one thread syncing up with an object running in another thread to hand off a piece of information, event or task. The `SynchronousQueue` class can be used to for implementing the direct handoff strategy. The `SynchronousQueue` doesn't have an internal capacity and an item can only be inserted in the  queue if another thread is simultaneously removing it.
Check: ThreadPoolExecutorWithSynchronousQueue.java

*Unbounded queues*:
The cons is that tasks get added to the queue if all the `corePoolSIze` threads are busy. Interestingly, the `maximumPoolSize` setting takes no effect and only `corePoolSize` threads are ever created. Submitted tasks sit in the queue waiting for execution.
Check: ThreadPoolExecutorWithLinkedBlockingQueue.java

*Bounded queues*:
The middle of the spectrum is to define a queue with a certain capacity and also set a limit on maximum number of threads. Using a bounded queue with a finite maximum pool size helps prevent resource exhaustion in the system. However, using a large queue sizes and small pools minimizes CPU usage, OS resources and context-switching overhead, but can lead to artificially low throughput.