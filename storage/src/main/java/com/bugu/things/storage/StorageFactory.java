package com.bugu.things.storage;

import android.content.Context;

import java.lang.reflect.Type;

/**
 * Author by xpl, Date on 2021/2/6.
 */
public interface StorageFactory {
    Type type();

    String rootPath();

    Context context();

}
