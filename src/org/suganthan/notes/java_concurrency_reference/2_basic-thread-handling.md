### Joining Threads

```java
 class ThreadJoinDemo {
    public static void main(String[] args) {
        Thread innerThread = new Thread(() -> {
            while (true) {
                System.out.println("Say Hello over and over again");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                }
            }
        });
        innerThread.start();
        System.out.println("Main thread exiting.");
    }
}
```
In the above example if we want the main thread to wait for the `innerThread` to finish before proceeding forward, we can direct the main thread to suspend its execution by calling `join` method on the `innerThread` object right after we start the `innerThread`. 

```java
Thread innerThread = new Thread(...)
innerThread.start();
innerThread.join();
```

### Daemon Threads

A daemon thread runs in the background but **as soon as the main application thread exits, all daemon threads are killed by the JVM**.

```java
innerThread.setDaemon(true);
```

### Sleeping Threads

A thread can be made dormant for a specified period using the sleep method

```java
Thread.sleep(1000);
```

### Interrupting Threads

Imagine a thread sleeps forever or goes into an infinite loop, it can prevent the spawning thread from moving ahead because of the join call. Java allows us to force such a misbehaved thread to come to its sense by interrupting it.

```java
class ThreadInterruptExample {
    public static void main(String[] args) {
        Thread innerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Inner thread goes to sleep at "+ System.currentTimeMillis() / 1000);
                    Thread.sleep(1000 * 1000);
                } catch (InterruptedException interruptedException) {
                    System.out.println("Inner Thread interrupted at "+ System.currentTimeMillis() / 1000);
                }
            }
        });
        innerThread.start();
        System.out.println("Main thread sleeping at "+ System.currentTimeMillis() / 1000);
        Thread.sleep(5000);
        innerThread.interrupt();
        System.out.println("Main thread exiting at "+System.currentTimeMillis() / 1000);
    }
}
```