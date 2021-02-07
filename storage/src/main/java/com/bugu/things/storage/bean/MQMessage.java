package com.bugu.things.storage.bean;

/**
 * Author by xpl, Date on 2021/2/5.
 */
public class MQMessage<T> {
    private long time;
    private int type;
    private long index;
    private T data;

    public MQMessage() {
    }

    public MQMessage(long time, int type, long index, T data) {
        this.time = time;
        this.type = type;
        this.index = index;
        this.data = data;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
