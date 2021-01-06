Mutex:
======

    Mutex allows only a single thread to access a resource or critical section.

    Once a thread acquires mutex, all other threads attempting to acquire the same mutex
    are blocked until the first thread releases the mutex.

Semaphore:
===========

    Semaphore, on the other hand, is used for limiting access to a collection of resources.
    Semaphores can also be used for signaling among threads. This is an important distinction as it allows threads
    to cooperatively work towards completing a tasks.

Difference:
============

    In case of a mutex the same thread must call acquire and subsequent release on the mutex, whereas in case of
    a binary semaphore, different threads can call acquire and release on the semaphore.

    A mutex is owned by the thread acquiring it till the point the owning-thread releases it,
    whereas for a semaphore there's no notion of ownership.