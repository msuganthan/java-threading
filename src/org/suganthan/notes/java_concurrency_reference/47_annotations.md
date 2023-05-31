<!-- TOC -->
  * [Annotations](#annotations)
    * [Overview](#overview)
    * [Class level annotations](#class-level-annotations)
      * [@ThreadSafe](#threadsafe)
      * [@NotThreadSafe](#notthreadsafe)
      * [@Immutable](#immutable)
    * [Field and method-level annotations](#field-and-method-level-annotations)
      * [@GuardedBy("lock")](#guardedbylock)
        * [@GuardedBy("this")](#guardedbythis)
        * [@GuardedBy("field")](#guardedbyfield)
        * [@GuardedBy("ClassName.fieldName")](#guardedbyclassnamefieldname)
        * [@GuardedBy("methodName()")](#guardedbymethodname)
        * [@GuardedBy("ClassName.class")](#guardedbyclassnameclass)
    * [Summary](#summary)
<!-- TOC -->

## Annotations
### Overview
Just going to explore various Java annotations that are related to concurrency.
### Class level annotations

Note that these annotations serve as documentation about class behavior but don’t change the ability or functionality of class in any way.

#### @ThreadSafe

The annotation `@ThreadSafe` can be applied to a class to indicate to users and maintainers that class is thread safe and multiple thread can concurrently interact with an instance of the class without worrying about synchronization.

#### @NotThreadSafe

The @NotThreadSafe annotation is the opposite of @ThreadSafe and is intended to explicitly communicate to the users and maintainers that the class requires synchronization effort on part of the users to ensure thread-safe concurrent access to instances of the class.

#### @Immutable

The @Immutable annotation indicates that once an object of the class is instantiated, it can’t be changed. All operations on such an instance of the class become read-only operations and consequently the class becomes thread safe. In other words, @Immutable implies @ThreadSafe.

### Field and method-level annotations
#### @GuardedBy("lock")

The annotation `@GuardedBy(lock)` can be used to document that a certain field or method should only be accessed while holding the lock argument in the annotation. The `lock` argument passed-in to the annotation can take-on different values

```java
public class AnnotationsExampleClass {

    // field is protected by the method instance
    @GuardedBy("this") // example of @GuardedBy("this")
    private String[] stringValues;

    private Object customLock = new Object();

    private static ReentrantLock counterLock = new ReentrantLock();

    @GuardedBy("AnnotationsExampleClass.class") // example of @GuardedBy("ClassName.class")
    private static long lastAccess = System.currentTimeMillis();

    private void updateAccessTime() {
        synchronized (AnnotationsExampleClass.class) {
            lastAccess = System.currentTimeMillis();
        }
    }

    public void manipulateStringValues() {
        // This method holds the "this" lock before
        // accessing the stringValues variable.
        synchronized (this) {
            // ... modify array of Strings here
        }
    }

    // method is protected by the object instance
    @GuardedBy("customLock") // example of @GuardedBy("field")
    private String getStringValue(int index) {
        return stringValues[index];
    }

    // This method acquires two locks
    synchronized public void printValues() {

        for (int i = 0; i < stringValues.length; i++) {
            synchronized (customLock) {
                getStringValue(i);
            }
        }
    }

    // method used to return an appropriate lock based on the input argument.
    private Object lockMaster(int input) {
        switch (input) {

            case 1:
                return customLock;

            // ... other cases
        }

        return null;
    }

    @GuardedBy("lockMaster(1)") // @GuardedBy("methodName()")
    private void transforms() {
        // ... method should be invoked only after acquiring the lock
        // ... returned by the lockMaster method
    }


    static class Helper {
        @GuardedBy("AnnotationsExampleClass.counterLock") // example of @GuardedBy("ClassName.fieldName")
        private static int counter = 0;


        public void incrementCounter() {
            try {
                AnnotationsExampleClass.counterLock.lock();
                counter++;
            } finally {
                AnnotationsExampleClass.counterLock.unlock();
            }
        }
    }
}
```

##### @GuardedBy("this")

Indicates that the field or method should be accessed while holding the intrinsic lock, i.e. the object itself of which the field or method is a member. In our example above the field `stringValues` uses this annotation and in the method `manipulateStringValues()` the intrinsic lock is acquired before manipulating `stringValues`.

##### @GuardedBy("field")

Indicates that the field or method should be accessed while synchronizing on the lock associated with the named field in the annotation. In our example class `AnnotationsExampleClass`, the field customLock appears as the named field in the annotation for the method getStringValue(). This method is in turn invoked by printValues() which acquires the intrinsic lock associated with the customLock field before invoking the getStringValue() method.

If the named field in the annotation were a lock object itself, e.g. if customLock were an instance of the ReentrantLock class the we’d acquire the explicit lock, i.e. customLock.lock() rather than the intrinsic lock associated with the object.

##### @GuardedBy("ClassName.fieldName")

This form of annotation is similar to the `@GuardedBy("field")`, however, the lock argument represents an object held in a static field of another class. In our example, the inner `Helper` class’s `counter` field uses the annotation to indicate to the users to acquire the `counterLock` of the `AnnotationsExampleClass` before manipulating the `counter` object.

##### @GuardedBy("methodName()")

Indicates that the lock returned by the named method should be acquired before interacting with the annotated field or method. In our example class, the method `lockMaster()` is named in the annotation for the method `transforms()`. The annotation implies that `lockMaster(1)` should be invoked, the returned lock acquired and then only `transforms()` should be invoked.

##### @GuardedBy("ClassName.class")

Indicates that the lock returned by the named method should be acquired before interacting with the annotated field or method. In our example class, the method lockMaster() is named in the annotation for the method transforms(). The annotation implies that lockMaster(1) should be invoked, the returned lock acquired and then only transforms() should be invoked.

### Summary