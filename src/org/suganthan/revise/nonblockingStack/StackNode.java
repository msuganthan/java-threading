package org.suganthan.revise.nonblockingStack;

public class StackNode<T> {
    private T item;
    private StackNode<T> next;

    public StackNode(T item) {
        this.item = item;
    }

    public StackNode<T> getNext() {
        return next;
    }

    public void setNext(StackNode<T> stackNode) {
        next = stackNode;
    }

    public T getItem() {
        return this.item;
    }
}
