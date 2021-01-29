package com.bugu.queue.header;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Author by xpl, Date on 2021/1/27.
 */
public interface Pointer {
    long point();

    void write(RandomAccessFile raf, long value) throws IOException;

    long read(RandomAccessFile raf) throws IOException;
}
