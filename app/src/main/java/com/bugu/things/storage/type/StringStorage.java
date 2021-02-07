package com.bugu.things.storage.type;

import android.content.Context;

import com.bugu.things.annatation.StorageType;
import com.bugu.things.storage.App;
import com.bugu.things.storage.bean.MQMessage;
import com.bugu.things.storage.StorageFactory;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Author by xpl, Date on 2021/2/6.
 */
@StorageType(type = StorageFactory.class, path = "t01.txt", id = "id-1")
public class StringStorage implements StorageFactory {
    @Override
    public Type type() {
        return new TypeToken<MQMessage<String>>() {
        }.getType();
    }

    @Override
    public String rootPath() {
        return "xxx";
    }

    @Override
    public Context context() {
        return App.getINSTANCE();
    }
}
