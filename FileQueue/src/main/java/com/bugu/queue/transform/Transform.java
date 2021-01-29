package com.bugu.queue.transform;

import java.io.RandomAccessFile;

/**
 * Author by xpl, Date on 2021/1/27.
 */
public interface Transform<E> {
    void write(E e, RandomAccessFile raf) throws Exception;

    E read(RandomAccessFile raf) throws Exception;
}
