<!-- TOC -->
  * [ThreadLocal](#threadlocal)
<!-- TOC -->

## ThreadLocal

Consider the following instance method of a class

```java
void add(int val) {
    int count = 5;
    count += val;
    
}
```
The above method is thread safe, because each executing thread will create a copy of the local variable on its own thread stack. There would be no shared variables amongst the threads and the instance method by itself would be thread-safe.

However, if we move the `count` variable out of the method and declared it as an instance variable then the same code will not be thread-safe.

We can have copy of an instance variable for each thread that accesses it by declaring the instance variable `ThreadLocal`. Look at the thread unsafe code below. If you run it multiple times, you'll see different results. The count variable is incremented 100 times by 100 threads so in a thread-safe world the final value of the variable should out to be 10,000. 

```java
class Demo {
    public static void main(String[] args) {
        UnsafeCounter usc = new UnsafeCounter();
        Thread[] tasks = new Thread[100];
        for (int i = 0; i < 100; i++) {
            Thread t = new Thread(() -> {
                for (int i = 0; i < 100; i++) {
                    usc.increment();
                }
            });
            tasks[i] = t;
            t.start();
        }
        for (int j = 0; j < 100; j++) {
            tasks[i].join();
        }
        System.out.println(usc.count);
    }
}

class UnsafeCounter {
    int count = 0;
    
    void increment() {
        count = count + 1;
    }
}
```

Now we'll change the code to make the instance variable threadLocal. The change is:

```java
ThreadLocal<Integer> counter = ThreadLocal.withInitial(() -> 0);
```

The above code creates a separate and completely independent copy of the variable `counter` for every thread that calls the `increment()` method. Conceptually, you can think of a `ThreadLocal<T>` variable as a `map` that contains mapping for each thread and its copy of the threadLocal variable or equivalently a `Map<Thread, T>`. Though this is not how it is actually implemented. Furthermore, the thread specific values are stored in the thread object itself and are eligible for garbage collection once a thread terminates.

ThreadLocal variables get tricky when used with the executor service since threads that don't terminate aren't returned to the threadPool. So any threadLocal variables for such threads aren't garbage collected.

```java
class Demo {
    public static void main(String[] args) {
        UnsafeCounter usc = new UnsafeCounter();
        Thread[] tasks = new Thread[100];

        for (int i = 0; i < 100; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    usc.increment();
                }
                System.out.println(usc.counter.get());
            });
            tasks[i] = t;
            t.start();
        }

        for (int i = 0; i < 100; i++) {
            tasks[i].join();
        }
        System.out.println(usc.counter.get());
    }
}
class UnsafeCounter {
    ThreadLocal<Integer> counter = ThreadLocal.withInitial(() -> 0);
    
    void increment() {
        counter.set(counter.get() + 1); 
    }
}
```