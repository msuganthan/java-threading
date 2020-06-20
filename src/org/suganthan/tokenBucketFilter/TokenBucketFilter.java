package org.suganthan.tokenBucketFilter;

import java.util.HashSet;
import java.util.Set;

public class TokenBucketFilter {
    private int MAX_TOKENS;
    private long lastRequestTime = System.currentTimeMillis();
    long possibleTokens = 0;

    public TokenBucketFilter(int maxTokens) {
        MAX_TOKENS = maxTokens;
    }

    synchronized void getToken() throws InterruptedException {
        //Divide by a 1000 to get granularity at the second level.
        possibleTokens += (System.currentTimeMillis() - lastRequestTime) / 1000;

        if (possibleTokens > MAX_TOKENS)
            possibleTokens = MAX_TOKENS;

        if (possibleTokens == 0)
            Thread.sleep(1000);
        else
            possibleTokens--;
        lastRequestTime = System.currentTimeMillis();

        System.out.println("Granting "+ Thread.currentThread().getName() + " Token at "+ (System.currentTimeMillis() / 1000));
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
            thread.setName("Thread_"+(i+1));
            allThreads.add(thread);
        }

        for (Thread t: allThreads)
            t.start();

        for (Thread t: allThreads)
            t.join();
    }

    public static void main(String[] args) throws InterruptedException {
        runTextMaxTokenIs1();
    }
}
