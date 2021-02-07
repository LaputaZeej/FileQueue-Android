package com.bugu.things.storage.test;

import android.content.Context;

import com.bugu.things.storage.Constant;
import com.bugu.things.storage.core.FileQueueStorage;
import com.bugu.things.storage.bean.MQMessage;
import com.bugu.things.storage.core.Storage;
import com.google.gson.reflect.TypeToken;

/**
 * Author by xpl, Date on 2021/2/3.
 */
public class DemoStorageImpl implements Storage<String> {

    private static final String TAG = Constant.TAG + "Demo";
    private Storage<String> mStorage = null;

    private DemoStorageImpl() {

    }

    public DemoStorageImpl init(Context context, String path) {
        String file = context.getFilesDir().getAbsolutePath() + "/" + path;
        mStorage = new FileQueueStorage<String>(context, file, 0, 0, new TypeToken<MQMessage<String>>() {
        });
        return DemoStorageImpl.getInstance();
    }

    @Override
    public void put(String data) {
        mStorage.put(data);
    }

    @Override
    public String take() {
        return mStorage.take();
    }

    @Override
    public void delete() {
        mStorage.delete();
    }

    public static DemoStorageImpl getInstance() {
        return Holder.INSTANCE;
    }

    public static final class Holder {
        private static final DemoStorageImpl INSTANCE = new DemoStorageImpl();
    }


}
