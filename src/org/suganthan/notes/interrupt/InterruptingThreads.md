Interrupting Threads:
=====================

    When a thread wait()-s or sleep()-s then one way for it to give up waiting/sleeping is to be interrupted. If a
    thread is interrupted while waiting/sleeping, it'll wake up and immediately throw Interrupted exception.

    The thread class exposes the interrupt() method which can be used to interrupt a thread that is blocked in a
    sleep() or wait() call. Note that invoking the interrupt method only sets a flag that is polled periodically
    by sleep or wait to know the current thread has been inter