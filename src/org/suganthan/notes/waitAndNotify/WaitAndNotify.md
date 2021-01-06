Wait:
=====
The wait method is exposed on each Java Object. Each Java object can act as a condition variable. When a thread
executed the wait method, it releases the monitor for the object and is place in the wait queue.

Note: That the thread must be inside a synchronized block of code that synchronizes on the same object as the one
on which wait() is being called, or in other words, the thread must hold the monitor of the object on which it'll
call wait. If not so, an illegalMonitor exception is raised.

Notify:
=======
Like the wait method, notify() can only we called by the thread which owns the monitor for the object on which
notify is being called else an illegal monitor exception is thrown. The notify method, will awaken one of the threads
in the associated wait queue, i.e. waiting on the thread monitor.

