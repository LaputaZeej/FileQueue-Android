package com.bugu.things.storage.type;

import android.content.Context;

import com.bugu.things.storage.core.FileQueueStorage;
import com.bugu.things.storage.core.Storage;

import java.lang.reflect.Type;

/**
 * Author by xpl, Date on 2021/2/6.
 */
public class StringStorageSingleton {
    private Storage<String> mStorage = null;

    private StringStorageSingleton() {
        StringStorage stringStorage = new StringStorage();
        String path = stringStorage.rootPath();
        Type type = stringStorage.type();
        Context context = stringStorage.context();
        mStorage = new FileQueueStorage<String>(context,path,0,0,type);
    }

    public void put(String data) {
        mStorage.put(data);
    }

    public String take() {
        return mStorage.take();
    }

    public void delete() {
        mStorage.delete();
    }

    public static StringStorageSingleton getInstance() {
        return StringStorageSingleton.Holder.INSTANCE;
    }

    public static final class Holder {
        private static final StringStorageSingleton INSTANCE = new StringStorageSingleton();
    }
}
