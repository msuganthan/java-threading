**newFixedThreadPool**: This type of pool has a fixed number of threads and any number of task can be submitted
execution. Once a thread finishes a task, it can **reused** to execute another task from the queue.

**newSingleThreadExecutor**: This executor uses a single worker thread to take tasks off of queue 
and execute them. If the thread dies unexpectedly, then the executor will replace it with a new one.

**newCachedThreadPool**: This pool will create new threads as required as use older once when they 
become available. However, it will terminate thread that remains idle for a certain configurable 
period of time to conserve memory. This pool can be a good choice for short-lived asynchronous tasks.

**newScheduledThreadPool**: This pool can be used to execute task periodically or after a delay.

**ForkJoinPool**: These pools are used for tasks which fork into smaller subtasks and then join results
once the subtasks are finished to a give an uber result. Its essentially then divide and conquer paradigm
applied to tasks.

**Lifecycle of an executor**:
    Running
    Shutting Down
    Terminated