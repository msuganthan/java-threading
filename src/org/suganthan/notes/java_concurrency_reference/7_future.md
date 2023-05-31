<!-- TOC -->
  * [Future interface](#future-interface)
<!-- TOC -->

## Future interface

The `Future` interface is used to represent to result of an asynchronous computation. The interface also provides methods to check the status of the submitted task and also allows the task to be cancelled if possible.

```java
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class Demo {
    static ExectorService exectorService = Executors.newFixedThreadPool(2);

    static int pollingStatusAndCancelTask(final int n) {
        int result = -1;

        Callable<Integer> sumTask = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(10);
                int sum = 0;
                for (int i = 0; i < n; i++) {
                    sum += i;
                }
                return sum;
            }
        };

        Callable<Void> randomTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(3600 * 1000);
                return null;
            }
        };

        Future<Integer> f1 = exectorService.submit(sumTask);
        Future<Void> f2 = exectorService.submit(randomTask);

        try {
            f2.cancel(true);
            while (!f1.isDone()) {
                System.out.println("Waiting for first task to complete.");
            }
            result = f1.get();
        } catch (ExecutionException ee) {
            System.out.println("Something went wrong");
        }
        System.out.println("\nIs second task cancelled : " + f2.isCancelled());
        return result;
    }
}
```