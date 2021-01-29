package com.bugu.queue.android;

import android.content.Context;

import com.bugu.queue.transform.GsonTransform;
import com.bugu.queue.transform.ProtobufTransform;
import com.google.protobuf.MessageLite;

/**
 * Author by xpl, Date on 2021/1/27.
 */
public class Ext {

    public static <E extends MessageLite> AndroidFileQueue<E> createProtobufFileQueue(Context context, String path, Class<E> clz) {
        return new AndroidFileQueue<E>(context, path, new ProtobufTransform<E>(clz));

    }

    public static <E> AndroidFileQueue<E> createGsonFileQueue(Context context, String path, Class<E> clz) {
        return new AndroidFileQueue<E>(context, path, new GsonTransform<E>(clz));

    }
}
