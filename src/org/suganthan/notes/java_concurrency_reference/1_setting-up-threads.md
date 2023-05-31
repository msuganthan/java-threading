<!-- TOC -->
  * [Setting up thread](#setting-up-thread)
    * [Runnable Interface](#runnable-interface)
    * [Subclassing Thread class](#subclassing-thread-class)
<!-- TOC -->

## Setting up thread

### Runnable Interface

Anonymous
```
Thread thread = new Thread(new Runnable() {
    @Override
    public void run() {
        System.out.println("Say Hello!!!");    
    }
});
thread.start();
```

```
class ExecuteMe implements Runnable {
    @Override
    public void run() {
        System.out.println("Say Hello!!!");
    }
};
Thread thread = new Thread(new ExecuteMe());
thread.start();
```

### Subclassing Thread class

```java
class ExecuteMe extends Thread {
    @Override
    public void run() {
        System.out.println("I ran after extending thread class.");
    }
}

```