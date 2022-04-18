package org.suganthan.threadingProblems.basics.completionService;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Imagine a scenario where you want to submit hundreds or thousands of tasks. You'll retrieve the future objects returned from the submit calls and then poll all of them in a loop to check which one is done and then take appropriate action. Java offers a better way to address this use case through the `CompletionService` interface. You can use the `ExecutorCompletionService` as a concrete implementation of the interface.
 *
 * The completion service is a combination of blocking queue and an executor. Tasks are submitted to the queue and then the queue can be polled for compelted tasks. The service exposes two methods one `poll` which returns null if no task is completed or none were submitted and two `take` which blocks till a completed task is available.
 */
public class CompletionServiceTest {

    static Random random = new Random(System.currentTimeMillis());
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        completionServiceExample();
    }

    static void completionServiceExample() throws ExecutionException, InterruptedException {
        class TrivialTask implements Runnable {
            int n;
            public TrivialTask(int n) {
                this.n = n;
            }
            @Override
            public void run() {
                try {
                    Thread.sleep(random.nextInt(101));
                    System.out.println(n * n);
                } catch (InterruptedException ie) {}
            }
        }

        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        ExecutorCompletionService<Integer> service =
                new ExecutorCompletionService<>(threadPool);

        //Submit 10 trivial tasks
        for (int i = 0; i < 10; i++) {
            service.submit(new TrivialTask(i), i + 5);
        }

        int count = 10;
        while (count != 0) {
            Future<Integer> future = service.poll();
            if (future != null) {
                System.out.println("Thread "+future.get() + " got done.");
                count--;
            }
        }
        threadPool.shutdown();
    }
}
