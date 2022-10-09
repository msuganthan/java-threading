Semaphores:
===========

Java's semaphores can be `release()`-ed or `acquire()`-d for signaling amongst threads. However, the important callout when using the semaphores is to make sure to **that the permits acquired should equal to the permits returned.**

```java
import java.util.concurrent.Semaphore;

class IncorrectSemaphoreExample {
    public static void example() throws InterruptedException {
        final Semaphore semaphore = new Semaphore(1);

        Thread badThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        semaphore.acquire();
                    } catch (InterruptedException ie) {
                    }

                    // Thread was meant to run forever but runs into exception that causes the thread to crash.
                    throw new RuntimeException("Exception happens at runtime.");

                    //The following line to signal the semaphore is never reached
                    //semaphore.release();
                }
            }
        });

        badThread.start();

        Thread.sleep(1000);

        final Thread goodThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Goot thread patiently waiting to be signalled");
                semaphore.acquire();
            }
        });
        
        goodThread.start();
        
        badThread.join();
        goodThread.join();
        System.out.println("Exiting Program");
    }
}
```

The above code when run would teim out and show that one of thre threads threw an exception. The code is never able to release the sempahore cause the other thred to block forever. Whenever using locaks or semaphore, remember to unlock or release the semaphore in a `finally` block. The corrected version appeards below.

```java
import java.util.concurrent.Semaphore;

class CorrectSemphoreExample {
    public static void example() throws InterruptedException {
        final Semaphore semaphore = new Semaphore(1);

        Thread badThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        semaphore.acquire();
                        try {
                            throw new RuntimeException();
                        } catch (Exception e) {
                            return;
                        } finally {
                            System.out.println("Bad thread releasing semaphore.");
                            semaphore.release();
                        }
                    } catch (InterruptedException ie) {
                    }
                }
            }
        });
        badThread.start();

        Thread.sleep(1000);

        final Thread goodThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Good thread patiently waiting to be signalled.");
                try {
                    semaphore.acquire();
                } catch (InterruptedException ie) {
                    
                }
            }
        });
        
        goodThread.start();
        badThread.join();
        goodThread.join();
        System.out.println("Exiting program...");
    }
}
```