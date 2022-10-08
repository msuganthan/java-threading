package org.suganthan.revise.tokenBucketFilter;

public class TokenBucketFiller {
    private long possibleTokens;
    private int MAX_TOKENS;
    private long lastRequestTime = System.currentTimeMillis();

    public TokenBucketFiller(int maxTokens) {
        MAX_TOKENS = maxTokens;
    }

    synchronized void getToken() throws InterruptedException {
        possibleTokens += (System.currentTimeMillis() - lastRequestTime) / 1000;

        if (possibleTokens == MAX_TOKENS)
            possibleTokens = MAX_TOKENS;

        if (possibleTokens == 0) {
            Thread.sleep(1000);
        } else {
            possibleTokens--;
        }

        lastRequestTime = System.currentTimeMillis();
    }
}
