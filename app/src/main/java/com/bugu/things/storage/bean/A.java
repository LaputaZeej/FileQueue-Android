package com.bugu.things.storage.bean;

/**
 * Author by xpl, Date on 2021/2/8.
 */
public class A<T> {
    private T b;

    public T getB() {
        return b;
    }

    public void setB(T b) {
        this.b = b;
    }

    public A(T b) {
        this.b = b;
    }

    @Override
    public String toString() {
        return "A{" +
                "b=" + b +
                '}';
    }
}
