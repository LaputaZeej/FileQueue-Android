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
}