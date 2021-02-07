package com.bugu.things.storage.core;

import android.content.Context;

/**
 * Author by xpl, Date on 2021/2/5.
 */
public interface Storage<T> {

    void put(T data);

    T take();

    void delete();
}
