CPU Bound:
==========

    Programs which are compute-intensive i.e. program execution requires very high utilization of the CPU are called
    CPU bound programs. Such programs primarily depends on improving CPU speed to decrease program completion time.
    This could include programs such as data crunching, image processing, matrix multiplication etc.

    Structure your program code that can take advantage of the multiple CPU units available.

I/O Bound:
==========

    I/O bound programs spend most of their time waiting for input or output operations to complete while the CPU sits
    idle.

    I/O operation consists of operations that write or read from main memory or network interfaces.

Notes:
======

    CPU bound we can increase the number of processors and structure of our program to spawn multiple threads than
    individually run on a dedicated or shared CPU.

    For I/O bound programs, it makes sense to have a thread give up the CPU control if it is waiting for an I/O
    operation to complete so that another thread can get scheduled on the CPU and utilize CPU cycles.