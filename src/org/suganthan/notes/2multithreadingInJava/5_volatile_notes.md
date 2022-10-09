**Volatile:**

**If you have a variable say counter, that is being worked on by a thread, it is possible the thread keeps a copy of the counter variable in the CPU cache, and manipulates it rather than writing to the main memory. The JVM will decide when to update the value to the main memory with the value of the counter, even though other thread may read the value from main memory and end up in reading a stale value.**

If a variable is declared as volatile then whenever a thread writes or reads to the volatile variable, **the read and write happens in the main memory**. **As a further guarantee, all the variables that are visible to writing thread also get written-out to the main memory alongside the volatile variable.** Similarly, all tje variables visible to the reading thread alongside the volatile variable will have the latest values visible to the reading thread.

```java
class VolatileExample {
    boolean flag = false;
    
    void  threadA() {
        while (!flag) {
            //...
        }
    }
    
    void threadB() {
        flag = true;
    }
}
```

 In the above program, we would expect that `threadA` would exit the `while` loop once `threadB` sets the variable `flag` to true but `threadA` may unfortunately find itself spinning forever if it has cached the variable flag's value. In this scenario, marking flag as volatile will fix the problem. No that volatile present a consisten view of the memory to all the threads. However, remember **that volatile doesn't imply or mean thread-safety.** 

Consider the program below where we declare the variable `count volatile` and several threads increment the variable 1000 times each. If you run the program several times you'll see `count` summing up to the values other than the expected 10, 000.

```java
class Demonstration {
    static volatile int count = 0;

    public static void main(String[] args) {
        int numThreads = 10;
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 1000; i++) {
                        count++;
                    }
                }
            });
        }

        for (int i = 0; i < numThreads; i++) {
            threads[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }

        System.out.println("Count = "+ count);
    }
}
```

**When is volatile thread safe:**

Volatile comes into play because of multiple level of memory in hardware architecture required for performance enhancements. If there's a single thread that writes to the volatile variable and other threads only read the volatile variable then just using volatile is enough, however if there's a possibility of multiple threads writing to volatile then `synchronized` would be required to ensure atomic writes to the variable.