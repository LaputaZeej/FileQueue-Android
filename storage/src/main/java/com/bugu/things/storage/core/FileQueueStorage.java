package com.bugu.things.storage.core;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.bugu.queue.FileQueue;
import com.bugu.queue.MutableFileQueue;
import com.bugu.things.storage.Constant;
import com.bugu.things.storage.bean.MQMessage;
import com.bugu.things.storage.util.Utils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Author by xpl, Date on 2021/2/5.
 */
public class FileQueueStorage<T> extends AbsStorage<T> {
    private static final String TAG = Constant.TAG + "StorageImpl";
    private String path;
    private FileQueue<MQMessage<T>> mFileQueue;

    public FileQueueStorage(Context context, String path, long capacity, long max, Type type) {
        super(context);
        this.path = path;
        checkPath(path);
        this.mFileQueue = new MutableFileQueue.Builder()
                .maxSize(max)
                .path(path)
                .capacity(capacity)
                .type(type)
                .build();
    }
    public FileQueueStorage(Context context, String path, long capacity, long max, TypeToken<MQMessage<T>> typeToken) {
        super(context);
        this.path = path;
        checkPath(path);
        this.mFileQueue = new MutableFileQueue.Builder()
                .maxSize(max)
                .path(path)
                .capacity(capacity)
                .type(typeToken.getType())
                .build();
    }

    private void checkPath(String path) {
        if (!Utils.isPrivateDirectory(context, path)) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                throw new IllegalStateException("请先申请权限 ：" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    @Override
    public void put(T data) {
        checkPath(this.path);
        MQMessage<T> message = new MQMessage<>();
        message.setData(data);
        message.setIndex(1);
        message.setTime(System.currentTimeMillis());
        message.setType(0);
        try {
            Log.i(TAG, "put -> " + message);
            mFileQueue.put(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public T take() {
        checkPath(this.path);
        MQMessage<T> take = null;
        try {
            take = mFileQueue.take();
            Log.i(TAG, "take -> " + take.getData());
            return take.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete() {
        checkPath(this.path);
        mFileQueue.delete();
    }
}
