package com.bugu.queue.transform;

import com.google.gson.Gson;

import java.io.RandomAccessFile;

/**
 * Author by xpl, Date on 2021/1/27.
 */
public class GsonTransform<E> extends AbsTransform<E> {
    private Gson gson;
    private Class<E> clz;

    public GsonTransform(Class<E> clz) {
        this.clz = clz;
        this.gson = new Gson();
    }

    @Override
    public void write(E e, RandomAccessFile raf) throws Exception {
        String json = gson.toJson(e);
        write0(raf, json.getBytes());
    }

    @Override
    public E read(RandomAccessFile raf) throws Exception {
        byte[] read = read0(raf);
        String json = new String(read);
        return gson.fromJson(json, clz);
    }
}
