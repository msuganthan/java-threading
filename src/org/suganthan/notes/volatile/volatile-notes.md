Volatile:
=========

    If you have a variable say counter, that is being worked on by a thread, it is possible the thread keeps a copy
    of the counter variable in the CPU cache, and manipulates it rather than writing to the main memory.

    The JVM will decide when to update the value to the main memory, even though other thread may read the value from
    main memory and end up in reading a stale value.

    If a variable is declared as volatile then whenever a thread writes or reads to the volatile variable, the read
    and write happens in the main memory. As a further guarantee, all the variables that are visible to writing thread
    also get written-out to the main memory alongside the volatile variable.

    If there is a single thread that writes to the volatile variable and other threads only read the volatile variable
    then just using volatile is enough, however if there's a possibility of multiple threads writing to the volatile
    variable then synchronized would be required to ensure the atomic writes to the variable.