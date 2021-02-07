package com.bugu.things.storage;

import com.google.gson.reflect.TypeToken;

import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Author by xpl, Date on 2021/2/4.
 */
public class JavaTest {
    @Test
    public void t1() {
        SingletonA.getInstance().test("hello");
        SingletonA.getInstance().test(1);
        SingletonA.getInstance().test(1f);
        ArrayList<String> list = new ArrayList<>();
        list.add("9");
        list.add("9");
        list.add("6");
        SingletonA.getInstance().test(list);
        SingletonA.getInstance().print();
        String s = SingletonA.getInstance().<String>get(0);
        System.out.println("s = " + s);
        String s1 = SingletonA.getInstance().<String>get(1);
        System.out.println("s1 = " + s1);
    }

    @Test
    public void t2() {
        Type type = new TypeToken<List<String>>() {
        }.getType();
        Type type2 = new TypeToken<List<Integer>>() {
        }.getType();
        Type type3 = new TypeToken<List<String>>() {
        }.getType();
        System.out.println(type);
        System.out.println(type2);
        System.out.println(type==type2);
        System.out.println(type==type3);
    }
}
