package org.suganthan.threadingproblems.tokenBucketFilter;

import java.util.HashSet;
import java.util.Set;

public class TokenBucketFilter {
    long possibleTokens = 0;
    private int MAX_TOKENS;
    private long lastRequestTime = System.currentTimeMillis();

    public TokenBucketFilter(int maxTokens) {
        MAX_TOKENS = maxTokens;
    }

    public static void main(String[] args) throws InterruptedException {
        runTextMaxTokenIs1();
    }

    static void runTextMaxTokenIs1() throws InterruptedException {
        Set<Thread> allThreads = new HashSet<>();
        final TokenBucketFilter tokenBucketFilter = new TokenBucketFilter(5);

        // Sleep for 10 seconds.
        Thread.sleep(10000);

        for (int i = 0; i < 12; i++) {
            Thread thread = new Thread(() -> {
                try {
                    tokenBucketFilter.getToken();
                } catch (InterruptedException ie) {
                    System.out.println("We have a problem");
                }
            });
            thread.setName("Thread_" + (i + 1));
            allThreads.add(thread);
        }

        for (Thread t : allThreads)
            t.start();

        for (Thread t : allThreads)
            t.join();
    }

    synchronized void getToken() throws InterruptedException {
        /**
         * 1. lastRequestTime was initialized to System.currentTimeMillis during the start of the program.
         * 2. There is Thread.sleep(10000) in runTextMaxTokenIs1 method, the delay makes some token to generate.
         */
        possibleTokens += (System.currentTimeMillis() - lastRequestTime) / 1000;

        if (possibleTokens > MAX_TOKENS)
            possibleTokens = MAX_TOKENS;

        if (possibleTokens == 0)
            Thread.sleep(1000);
        else
            possibleTokens--;
        lastRequestTime = System.currentTimeMillis();

        System.out.println("Granting " + Thread.currentThread().getName() + " Token at " + (System.currentTimeMillis() / 1000));
    }
}
