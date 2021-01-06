package org.suganthan.revise.tokenBucketFilter;

public class MultithreadedTokenBucketFilter {
    private long possibleTokens = 0;
    private final int MAX_TOKENS;
    MultithreadedTokenBucketFilter(int maxTokens) {
        this.MAX_TOKENS = maxTokens;
    }

    void initialize() {
        Thread dt = new Thread(this::daemonThread);
        dt.setDaemon(true);
        dt.start();
    }

    private void daemonThread() {
        while (true) {
            synchronized (this) {
                if (possibleTokens < MAX_TOKENS) {
                    possibleTokens++;
                }
                this.notify();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void getToken() throws InterruptedException {
        synchronized (this) {
            while (possibleTokens == 0) {
                wait();
            }
            possibleTokens--;
        }
        System.out.println(
                "Granting " + Thread.currentThread().getName() + " token at " + System.currentTimeMillis() / 1000);
    }
}
