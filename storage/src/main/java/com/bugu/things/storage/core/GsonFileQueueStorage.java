//package com.bugu.things.storage.core;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.util.Log;
//
//import androidx.core.content.ContextCompat;
//
//import com.bugu.queue.FileQueue;
//import com.bugu.queue.MutableFileQueue;
//import com.bugu.queue.transform.GsonTransform;
//import com.bugu.things.storage.Constant;
//import com.bugu.things.storage.bean.MQMessage;
//import com.bugu.things.storage.util.Utils;
//import com.google.gson.reflect.TypeToken;
//
///**
// * Author by xpl, Date on 2021/2/5.
// */
//public class GsonFileQueueStorage<T> implements Storage<byte[]> {
//    private static final String TAG = Constant.TAG + "==";
//    private Context context;
//    private String path;
//    private FileQueue<MQMessage<byte[]>> mFileQueue;
//
//    public GsonFileQueueStorage(Context context, String path, long capacity, long max) {
//        this.context = context.getApplicationContext();
//        this.path = path;
//        checkPath(path);
//        this.mFileQueue = new MutableFileQueue.Builder<MQMessage<byte[]>>()
//                .maxSize(max)
//                .path(path)
//                .capacity(capacity)
//                .transform(new GsonTransform<MQMessage<byte[]>>(new TypeToken<MQMessage<byte[]>>(){}.getType()))
//                .build();
//    }
//
//    private void checkPath(String path) {
//        if (!Utils.isPrivateDirectory(context, path)) {
//            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                throw new IllegalStateException("请先申请权限 ：" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            }
//        }
//    }
//
//    @Override
//    public void put(byte[] data) {
//        checkPath(this.path);
//        MQMessage<byte[]> message = new MQMessage<>();
//        message.setData(data);
//        message.setIndex(1);
//        message.setTime(System.currentTimeMillis());
//        message.setType(0);
//        try {
//            Log.i(TAG, "put -> " + message);
//            mFileQueue.put(message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public byte[] take() {
//        checkPath(this.path);
//        MQMessage<byte[]> take = null;
//        try {
//            take = mFileQueue.take();
//            return take.getData();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @Override
//    public void delete() {
//        checkPath(this.path);
//        mFileQueue.delete();
//    }
//
//}
