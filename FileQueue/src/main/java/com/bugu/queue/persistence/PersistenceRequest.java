package com.bugu.queue.persistence;

/**
 * Author by xpl, Date on 2021/2/7.
 */
public class PersistenceRequest {

    public PersistenceRequest(byte[] data) {
        this.data = data;
    }

    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
