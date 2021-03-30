package com.bugu.queue.persistence;

/**
 * Author by xpl, Date on 2021/2/7.
 */
public class PersistenceRequest {

    private byte[] data;

    public PersistenceRequest(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
