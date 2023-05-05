The interface `Callable` allows task to return results. 

```java
interface Callable<T> {
    T call() throws Exception;
}
```

```java
import java.util.concurrent.Callable;

class SumTask implements Callable<Integer> {
    int n;
    public SumTask(int n) {
        this.n = n;
    }
    
    public Integer call() throws Exception {
        int sum = 0;
        for (int i = 0; i < n; i++) {
            sum += i;
        }
        return sum;
    }
}
```