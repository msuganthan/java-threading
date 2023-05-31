<!-- TOC -->
  * [Completion Service](#completion-service)
<!-- TOC -->

## Completion Service

Java offers a better way to address a scenario where you want to submit hundreds or thousands of tasks, that is through `CompletionService` interface. You can use the `ExecutorCompletionService` as a concrete implementation of the interface. 

The completion service is a combination of a blocking queue and an executor. Tasks are submitted to the queue and then the queue can be polled for completed tasks. The service exposes two methods, one `poll` which returns null if no task is completed or none were submitted and two `take` which blocks till a completed task is available.

```java
import java.util.Random;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Demo {
    static Random random = new Random(System.currentTimeMillis());

    static void completionServiceExample() throws Exception {
        class TrivalTask implements Runnable {
            int n;

            public TrivalTask(int n) {
                this.n = n;
            }

            public void run() {
                try {
                    Thread.sleep(random.nextInt(101));
                    System.out.println(n * n);
                } catch (InterruptedException ie) {
                    //swallow exception
                }
            }
        }
    }

    ExecutorService threadPool = Executors.newFixedThreadPool(3);
    ExecutorCompletionService<Integer> service = new ExecutorCompletionService<>(threadPool);
    
    for(int i = 0; i < 10; i++) {
        service.submit(new TrivialTask(i), new Integer(i));
    }
    
    int count = 10;
    while(count != 0) {
        Future<Integer> future = service.poll();
        if (future != null) {
            System.out.println("Thread "+ future.get() + " got done.");
            count--;
        }
    }
    threadPool.shutdown();
}
```

