package com.bugu.queue;

import com.bugu.queue.header.Header;

public interface FileQueue<E> {
    void close();

    void put(E e) throws Exception;

    E take() throws Exception;

    Header getHeader();

    String getPath();

    boolean delete();

    boolean isClosed();

    public static class State {
        public static final int CLOSED = 0;
        public static final int INIT = 1;
        public static final int OPENED = 1 << 1;
        public static final int FULL = 1 << 2;
    }
}