### Overview: 

A thread pool is a group of threads instantiated and kept alive to execute submitted tasks. Thread pools can achieve better performance and throughput than creating an individual thread per task by circumventing the overhead associated with thread creation and destruction. Additionally, system resources can be better managed using a thread pool, which allows us to limit the number of threads in the system.

Generally the use of the ThreadPoolExecutor class is discouraged in the favor of thread pools that can be instantiated using the Executors factory methods.

1. Executors.newCachedThreadPool() => unbounded thread pool, with automatic thread reclamation
2. Executors.newFixedThreadPool() => fixed size thread pool
3. Executors.newSingleThreadExecutor() => single background thread
4. Executors.newScheduledThreadPool() => fixed size thread pool supporting delayed and period task execution.

### ThreadPoolExecutor constructor

```java
public ThreadPoolExecutor(int corePoolSize,
        int maximumPoolSize,
        long keepAliveTime,
        TimeUnit unit,
        BlockingQueue<Runnable> workQueue,
        RejectedExecutionHandler handler)
```
## corePoolSize and maximumPoolSize

These arguments together determine the number of threads that get created in the pool. The workflow is as follows:

* When the pool has less than `corePoolSize` threads and a new task arrives, **a new thread is instantiated even if other threads in the pools are idle**.
* When the pool has more than `corePoolSize` threads but less than `maximumPoolSize` threads **then a new thread is only created if the queue that holds the submitted tasks is full**.
* The **maximum** number of threads that can be created is capped by `maximumPoolSize`

A newly instantiated pool creates core threads only when tasks **start arriving in the queue**. However, this behavior can be tweaked by invoking one of the `preStartCoreThread()` or `preStartAllCoreThreads()` methods which is a good idea when creating a pool with a non-empty queue.

## Setting `corePoolSize` equal to `maximumPoolSize`

If we set corePoolSize equal to maximumPoolSize we effectively create a fixed size thread pool(newFixedThreadPool())

## Setting `maximumPoolSize` to an arbitrary high value

Setting `maximumPoolSize` to an unbounded value such as Integer.MAX_VALUE allows the pool to accommodate an arbitrary number of concurrent tasks.

## Keep-alive

A thread-pool will eliminate threads in excess of `corePoolSize` after `keepAliveTime` has elapsed.

## ThreadFactory

The pool creates new threads using a `ThreadFactory`. The user has the choice to pass-in a factory of their own choice or let the `ThreadFactory` class choose the default. Usually you would pass-in a thread factory argument if you want to change the thread-name, thread group, priority, daemon status etc.

## Queuing

The queue used to hold tasks submitted to the executor. The queue works in tandem with the pool's thread size.
    * If fewer than `corePoolSize` threads are running when a new task is submitted, the executor prefers adding a new thread rather than queueing the task.
    * If `corePoolSize` or more threads are running, the executor prefer queueing the task than creating a new thread.
    * If the queue is full and creating a new thread would exceed the  `maximumPoolSize` the submitted task is rejected by the executor.

### Queuing strategies

The choice of the queue we pass-in determines the queuing strategy for the executor. The queueing strategies are:

#### Direct Handoffs(SynchronousQueue)

Direct handoff design involves an object running in one thread syncing up with an object running in another thread to hand off a piece of information, event or task.

The `SynchronousQueue` class can use be used for implementing the direct handoff strategy. The `SynchronousQueue` doesn't have an internal capacity(not even 1) and an item can only inserted in the queue if another thread is simultaneously removing it. The execution for the below code times out as the main thread gets blocked.

```java
import java.util.concurrent.SynchronousQueue;

class Demo {
    public static void main(String[] args) {
        SynchronousQueue<Integer> synchronousQueue = new SynchronousQueue<>();
        synchronousQueue.put(7);
    }
}
```

Given this behavior it is obvious that if the `ThreadPoolExecutor` is initialized with `SynchronousQueue`, each new task submitted to the `ThreadPoolExecutor` is handed off by the queue to one of the pool threads for execution. However, if task submitted exceed `maximumPoolSize` the queue doesn't hold any tasks and if no free threads are available then the submitted tasks are rejected. 

Consider the example program below, where we set the `maximumPoolSize` to 5 and then attempt to submit 50 tasks. Each task sleeps for 1 second so the entire poll is hogged after 5 tasks are submitted. The 6th task when submitted has the executor throw the `RejectedExectionException` to indicate that the task can't be accepted for execution by the thread pool. The tasks can't be queued by the `SynchronousQueue` and if no free thread is available a new one must be created but if the number of threads has already reached the maximum allowed number then the task is rejected.

```java
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class Demo {
    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 5, 1, TimeUnit.MINUTES, new SynchronousQueue<>());
        int i = 0;
        try {
            for (; i <; i++) {
                threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            System.out.println("Thread "+Thread.currentThread().getName() + " at work.");
                            Thread.sleep(1000);
                        } catch (InterruptedException ie) {
                            //
                        }
                    }
                });
            }
        } catch (RejectedExecutionException ree) {
            System.out.println("Task "+(i + 1)+ " rejected.");
        } finally {
            threadPoolExecutor.shutdown();
            threadPoolExecutor.awaitTermination(1, TimeUnit.HOURS);
        }
    }
}
```
Using the direct handoff strategy requires that the maximum allowed threads for a thread pool should be unbounded e.g. Integer.MAX_VALUE to avoid tasks being rejected. However, this setting entails that the number of threads in the system can grow to be very large if tasks are submitted at a rate faster than they can be processed at. Direct handoff policy is useful when handling sets of requests that might have internal dependencies as lockups are avoided.

#### Unbounded queues

If we use a queue such as the `LinkedBlockingQueue` without a predefined capacity, the queue can arbitrarily grow in size. The consequence is that tasks get added to the queue if all the `corePoolSize` threads are busy. Interestingly, the `maximumPoolSize` setting takes no effect and only `corePoolSize` threads are ever created. Submitted tasks sit in the queu waiting for execution. Using this strategy we can see the queue size grow indefinitely in contrast to the direct handoff approach in which the number of threads can grow indefinitely. Consider the program below that uses the `LinkedBlockingQueue` without a defined capacity and only 5 tasks the same as `corePoolSize` execute at any time. The rest pile up in the queue.

```java
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class Demo {
    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 5, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>());
        int i = 0;
        try {
            for (; i < 20; i++) {
                threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            System.out.println("Thread "+Thread.currentThread().getName() + " at work.");
                            Thread.sleep(1000);
                        } catch (InterruptedException ie) {
                            //ignore for now
                        }
                    }
                });
            }
        } catch (RejectedExecutionException ree) {
            System.out.println("Task "+(i + 1)+ " rejected.");
        } finally {
            threadPoolExecutor.shutdown();
            
            //wait for the executor to shutdown
            threadPoolExecutor.awaitTermination(1, TimeUnit.HOURS);
        }
    }
}
```

From the above program, the only two threads, which is also the `corePoolsize` ever execute the submitted tasks. At a time, only two tasks are executed while the rest queue-up and the queue grows without any bounds.

We can also define a capacity when passing in the `LinkedBlockQueue`. In that scenario the executor can reject newly submitted tasks if the queue has reached capacity and `maximumPoolSize` threads have been created and are busy executing other tasks. Note that **with a defined capacity queue the setting `maximumPoolSize` becomes effective.**

```java
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class Demo {
    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 5, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(5));
        int i = 0;
        try {

        } catch (RejectedExecutionException ree) {
            System.out.println("Task "+ (i + 1));
        } finally {
            threadPoolExecutor.shutdown();
            threadPoolExecutor.awaitTermination(1, TimeUnit.HOURS);
        }
    }
}
```

In the above program the 11th task gets rejected and the first 10 get executed. This makes sense because the thread pool has a maximum of 5 threads, all of which start working on the first five submitted tasks. There-after the next 5 threads get queued up in the queue which has a maximum capacity of 5. When the 11th task gets submitted, all five task executing the first 5 tasks and the queue is full, therefore the 11th task is rejected.

#### Bounded queues

From the previous discussion that a tradeoff between the maximum threads and queue size exists. Constraining one allows the other to grow unbounded. 

* **The middle of the spectrum is to define a queue with a certain capacity and also set a limit on the maximum number of threads.** 
* **Using a bounded queue with a finite maximum pool size helps prevent resource exhaustion in the system.** 
* **However, using large queue sizes and small pools minimizes CPU usage, OS resources, and context-switching overhead, but can lead to artificially low throughput.**
* **In systems where threads occasionally block for I/O, a system may be able to schedule time for more threads than you otherwise allow.**
* **Using smaller queues generally requires larger pool sizes, which keeps CPUs busier but may encounter un-acceptable scheduling overhead, while also decreases throughput.**

#### Queue manipulation

## Task Rejection

If the executor becomes overwhelmed with tasks, it can reject newly submitted tasks. This occurs when the executor has a defined maximum pool size and a defined queue capacity and both resources hit their limits. Tasks can also be rejected with they are submitted to an executor that has already been shutdown. There are four different policies that can be supplied to the executor to determine the course of action when tasks can't be accepted any more. These policies are represented by found classes that extend the `RejectExecutionHandler` class. The executor invokes the `rejectedExection()` method of the supplied `RejectExecutionHandler` when a task is intended for rejection.

### ThreadPoolExecutor.AbortPolicy

The abort policy simply throws the runtime `RejectedExecutionException` when a task can't be accepted. The previous example demonstrates the use of the `ThreadPoolExecutor.AbortPolicy` when tasks get rejected if they can't be accommodated by the thread pool.

### ThreadPoolExecutor.CallerRunPolicy

According to this policy the thread invoking the `execute()` method of the executor itself runs the task. This mechanism serves to throttle the rate at while tasks are submitted as the submitting thread themselves end up executing the tasks they submit.

```java
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class Demo {
    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 5, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(5), new ThreadPoolExecutor.CallerRunsPolicy());

        int i = 0;
        try {
            for (; i < 20; i++) {
                threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            System.out.println("Thread "+ Thread.currentThread().getName() + " at works.");
                            Thread.sleep(1000);
                        } catch (InterruptedException ie) {
                            
                        }
                    }
                });
            }
        } catch (RejectedExecutionException ree) {
            System.out.println("Task " + (i + 1) + " rejected.");
        } finally {
            threadPoolExecutor.shutdown();

            threadPoolExecutor.awaitTermination(1, TimeUnit.HOURS);
        }
    }
}
```

Notice that in the output of the above program, when the thread pool can't accept any more tasks, the main thread that is submitting the tasks, is itself pulled-in to execute the submitted task. Consequently, **the submission of new tasks slows down** as the main thread now executes the task itself.

### ThreadPoolExecutor.DiscardPolicy

```java
class Demo {
    public static void main(String[] args) {
        
    }
}
```

A task that can't be executed is simply dropped.

```java
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class Demo {
    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 5, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(5), new ThreadPoolExecutor.DiscardPolicy());

        int i = 0;
        try {
            for (; i < 20; ++) {
                threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            System.out.println("Thread " + Thread.currentThread().getName() + " at work.");
                            Thread.sleep(1000);
                        } catch (InterruptedException ie) {

                        }
                    }
                });
            }
        } catch (RejectedExecutionException ree) {
            System.out.println("Task "+ (i + 1) + " rejected.");
        } finally {
            threadPoolExecutor.shutdown();
            threadPoolExecutor.awaitTermination(1, TimeUnit.HOURS);
        }
    }
}
```

### ThreadPoolExecutor.DiscardOldestPolicy

When a task can't be accepted for execution, this policy causes the oldest unhandled request/task to be discarded and then the execution is retried for the just submitted task. In case the executor is shutdown then the task is simply discarded.

```java
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class Demo {
    static class MyTask implements Runnable {
        private int taskNum;

        public MyTask(int taskNum) {
            this.taskNum = taskNum;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                //ignore
            }
            System.out.println("Hello this is thread " + Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 5, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(5), new ThreadPoolExecutor.DiscardOldestPolicy());

        int i = 0;
        try {
            for (; i < 20; i++) {
                threadPoolExecutor.execute(new MyTask(i + 1));
            }
        } catch (RejectedExecutionException ree) {
            System.out.println("Task "+(i + 1) + " rejected.");
        } finally {
            threadPoolExecutor.shutdown();
            threadPoolExecutor.awaitTermination(1, TimeUnit.HOURS);
        }
    }
}
```

### Shutting down

The `ThreadPoolExecutor` can be shutdown by invoking the `shutdown()` method. A pool that is no longer referenced and has no remaining threads will shut-down automatically. In case `shutdown()` isn't automatically invoked then the configuration must make sure that us-used threads eventually die by setting the corePoolSize to zero and choosing an appropriate `keepAliveTime` value. Another option if `corePoolSize` is set to non-zero is to use the `allowCoreThreadTimeOut(boolean)` method to have the time out policy apply to both core and non-core threads.

### Hooks

The ThreadPoolExecutor class also exposes protected overridable methods that derived classes can override. For instance: The `beforeExecute(Thread, Runnable)` and `afterExecute(Runnable, Throwable)` methods are called before and after execution of each task. These can be used to manipulate the execution environment; for example, reinitializing ThreadLocals, gathering statistics, or adding log entries etc. Similarly, the method `terminated()` can be overridden to perform any special processing that needs to be done once the Executor has fully terminated. Note that if any of the BlockingQueue methods, callbacks or hooks throw an exception, threads in the pool may fail, terminate abruptly and possibly get replaced.