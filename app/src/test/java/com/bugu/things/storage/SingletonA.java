package com.bugu.things.storage;

import com.google.common.base.Function;

import java.util.ArrayList;
import java.util.List;

/**
 * Author by xpl, Date on 2021/2/6.
 */
public class SingletonA {
    private List<Object> data;

    private SingletonA() {
        data = new ArrayList<>();
    }

    public <T> void test(T t) {
        data.add(t);
        System.out.println("this.hashCode = " + this.hashCode() + " , t = " + t);
    }

    public <T> T get(int index) {
        return (T) data.get(index);
    }

    public <T> void print() {
        System.out.println(" data.clz = " + data.getClass());
        for (int i = 0; i < data.size(); i++) {
            Object t = data.get(i);
            System.out.println(i + " -> " + t + " , clz = " + t.getClass());
        }
    }

    public static SingletonA getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final SingletonA INSTANCE = new SingletonA();
    }


}
