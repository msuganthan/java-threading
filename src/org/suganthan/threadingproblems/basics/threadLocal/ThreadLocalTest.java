package org.suganthan.threadingproblems.basics.threadLocal;

public class ThreadLocalTest {
    public static void main(String[] args) throws InterruptedException {
        UnsafeCounter counter = new UnsafeCounter();
        Thread[] task = new Thread[100];

        for (int i = 0; i < 100; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    counter.increment();
                }
                System.out.println(counter.counter.get());
            });
            task[i] = t;
            t.start();
        }
        for (int i = 0; i < 100; i++) {
            task[i].join();
        }
        System.out.println(counter.counter.get());
    }
}

class UnsafeCounter {
    ThreadLocal<Integer> counter = ThreadLocal.withInitial(() -> 0);

    void increment() {
        counter.set(counter.get() + 1);
    }
}
