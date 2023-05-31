<!-- TOC -->
  * [Task](#task)
  * [Thread Pools](#thread-pools)
    * [Types of Thread Pools](#types-of-thread-pools)
    * [Lifecycle of an executor:](#lifecycle-of-an-executor)
<!-- TOC -->

## Task

A task is a logical unit of work, usually a task should be independent of other tasks **so that it can be completed by a single thread**. A task can be represented by an object of a class implementing the `Runnable` interface.

## Thread Pools

Thread pools in Java are implementations of the `Executor` interface or any of its sub-interfaces. Thread pools allows us to decouple task submission and execution. We have the **option of exposing an executor's configuration while deploying an application or switching one executor for another seamlessly**.

A thread pool can be tuned for the size of threads it holds. A thread pool may also replace a thread if it dies of an un-excepted exception.

* There is no latency when a request is received and processed by a thread because no time is lost in creating a thread.
* The system will not go out of memory because thread are not created without any limits
* Fine-tuning the thread-pool will allow us to control the throughput of the system. We can have enough threads to keep all processors busy but not so many as to overwhelm the system.
* The application will degrade gracefully if the system is under load.

```java
void receiveAndExecuteClientOrderBest() {
    int expectedConcurrentOrders = 100
    Executor executor = Executors.newFixedThreadPool(expectedConcurrentOrders)
    
    while(true) {
        Order order = waitForNextOrder()
        executor.execute(() -> order.execute())
    }    
}
```

### Types of Thread Pools

* **newFixedThreadPool**: This type of pool has a fixed number of threads and any number of task can be submitted execution. Once a thread finishes a task, it can **reused** to execute another task from the queue.
* **newSingleThreadExecutor**: This executor uses a single worker thread to take tasks off of queue and execute them. If the thread dies unexpectedly, then the executor will replace it with a new one.
* **newCachedThreadPool**: This pool will create new threads as required as use older once when they become available. However, it will terminate thread that remains idle for a certain configurable period of time to conserve memory. This pool can be a good choice for short-lived asynchronous tasks.
* **newScheduledThreadPool**: This pool can be used to execute task periodically or after a delay.
* **ForkJoinPool**: These pools are used for tasks which fork into smaller subtasks and then join results once the subtasks are finished to a give an uber result. Its essentially then divide and conquer paradigm applied to tasks.

* Using thread pools we are able to control the **order** in which task is executed.
* The thread in which a task is executed
* The maximum number of task that can be executed concurrently
* Maximum number of task that can be queued for execution
* the selection criteria for rejecting tasks when the system is overloaded 
* Finally actions to take before or after execution of tasks.

### Lifecycle of an executor:
* Running
* Shutting Down
* Terminated

The JVM can't exit unless all non-daemon thread have terminated. Executor can be made to shut down either abruptly or gracefully. When doing the former, **the executor attempts to cancel all tasks in progress and doesn't work in any enqueued ones, whereas when doing the latter, the executor gives a chance for task already in execution to complete but also completes the enqueued tasks**.

If shutdown is initiated then the executor will refuse to accept new tasks and if any are submitted, they can be handled by providing a `RejectExecutionHandler`

