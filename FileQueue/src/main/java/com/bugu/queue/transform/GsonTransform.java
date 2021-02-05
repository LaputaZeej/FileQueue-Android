package com.bugu.queue.transform;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.RandomAccessFile;
import java.lang.reflect.Type;

/**
 * Author by xpl, Date on 2021/1/27.
 */
public class GsonTransform<E> extends AbsTransform<E> {
    private Gson gson;
    private Type type;

    public GsonTransform(Type type) {
        this.gson = new Gson();
        this.type = type;
    }

    public GsonTransform(TypeToken<E> typeToken) {
        this(typeToken.getType());
    }

    @Override
    public void write(E e, RandomAccessFile raf) throws Exception {
        String json = gson.toJson(e);
        //Logger.info("[write] json ——> " + json);
        write0(raf, json.getBytes());
    }

    @Override
    public E read(RandomAccessFile raf) throws Exception {
        byte[] read = read0(raf);
        String json = new String(read);
        return gson.fromJson(json, type);
    }
}
