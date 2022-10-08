**ThreadLocaL**:

`ThreadLocal<Integer> counter = ThreadLocal.withInitial(() -> 0)` 

The above code creates a separate and completely independent copy of the variable `counter` for every thread that calls the `increment()` method. Conceptually, you can think of a `ThreadLocal<T>` variable as a map that contains mapping for each thread and its copy of the threadLocal variable or equivalently a `Map<Thread, T>`. Furthermore, the thread specific values are stored in the thread object itself and are eligible for garbage collection once a thread terminates.

`ThreadLocal` variables get tricky when used with the `executor` service since threads that don't terminate aren't returned to the threadpool. So any `threadLocal` variables for such threads are garbage collected.