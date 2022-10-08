It can be used to block a single or multiple threads while other threads complete their operations.

A `CountDownLatch` object is initialized with the number of tasks/threads it is required to wait for. Multiple thread can block and wait for the `CountDownLatch` object to reach zero by invoking `await()` method. Every time a thread finishes its work, the thread invokes `countDown()` which decrements the counter by 1. Once the count reaches zero, threads waiting on the `await()` method are notified and resume execution.

The counter in the `CountDownLatch` cannot be reset making the `CountDownLatch` object unreusable. A `CountDownLatch` initialized with a count of `1` serves as an on/off switch where a particular thread is simply waiting fot its only partner to complete. Whereas a `CountDownLatch` object initialized with a count of `N` indicates a thread waiting for `N` threads to complete their work. However, a single thread can also invoke `countDown()` N times to unblock a thread more than once.

If the `CountDownLatch` is initialized with zero, the thread would not wait for any other thread(s) to complete. The count passed is basically the number of times `countDown()` must be invoked before threads can pass through `await()`. If the `CountDownLatch` has reached zero and `countDown()` is again invoked, the latch will remain released hence making no difference.

A thread blocked on `await()` can also be interrupted by another thread as long as it is waiting and the counter has not reached zero.

