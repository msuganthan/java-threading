ReEntrant Lock:
===============

    Java's answer to the traditional mutex is the re-entrant lock, which comes with additional bells and whistles.
    It is similar to the implicit monitor lock accessed when using synchronized methods or blocks.

    With the Re-entrant lock you are free to lock and un-lock in different methods but not with different threads.
    If you attempt to unlock a re-entrant lock object by a thread which didn't lock it initially, you'll get an
    IllegalMonitorStateException.

Condition Variables:
=====================

    You can think of Condition as factoring out these three methods(wait(), notify(), notifyAll()) of the object monitor
    into separate objects so that there can be multiple wait-sets per object.

    ** Re-entrant Lock replaces synchronized blocks or methods.
    ** Condition replaces the object monitor methods.

        Lock lock = new ReentrantLock();
        Condition myCondition  = lock.newCondition();

    In the synchronized paradigm, we could only have one wait-set associated with each object.