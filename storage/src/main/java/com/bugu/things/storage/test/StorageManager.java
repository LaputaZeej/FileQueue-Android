package com.bugu.things.storage.test;

import android.content.Context;

import com.bugu.things.storage.Constant;
import com.bugu.things.storage.core.Storage;

/**
 * Author by xpl, Date on 2021/2/3.
 */
public class StorageManager {

    private static final String TAG = Constant.TAG + "StorageManager";
    private Storage<?> mStorage = null;
    private Context context;

    private StorageManager() {
    }

   /* private HashMap<String, FileQueueStorage<?>> mStore = new HashMap<String, FileQueueStorage<?>>();
    private HashMap<String, TypeToken<?>> mToken = new HashMap<String, TypeToken<?>>();

    public <E> void put(String path, E data) {
        FileQueueStorage<?> fileQueueStorage = mStore.get(path);
        if (fileQueueStorage == null) {
            fileQueueStorage = new FileQueueStorage<>(
                    this.context,
                    path,
                    0L,
                    0L,
                    new TypeToken<MQMessage<E>>() {
                    }
            );
        }
        ((FileQueueStorage<E>) fileQueueStorage).put(data);
    }*/


    public void setStorage(Storage<?> storage) {
        this.mStorage = storage;
    }

    public <T>void put(T data) {
//        mStorage.put(data);
    }

    public Object take() {
        return mStorage.take();
    }


    public static  StorageManager getInstance() {
        return Holder.INSTANCE;
    }

    public static final class Holder {
        private static final StorageManager INSTANCE = new StorageManager();
    }


}
