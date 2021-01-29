package com.bugu.queue.header;

import com.bugu.queue.Version;
import com.bugu.queue.transform.HeaderTransform;
import com.bugu.queue.util.RafHelper;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * Author by xpl, Date on 2021/1/27.
 */
public class HeaderHelper {
    public static HeaderState validateHeader(Header header) {
        long version = header.getVersion();
        long head = header.getHead();
        long tail = header.getTail();
        long length = header.getLength();
        if (version != Version.VERSION) {
            return HeaderState.INVALID;
        }
        if (head == 0 || tail == 0 || length == 0 ||
                head > tail ||
                tail > length
        ) {
            return HeaderState.INVALID;
        }
        if (head == tail && head == Header.HEADER_LENGTH) {
            return HeaderState.INIT;
        }
        if (head < tail) {
            return HeaderState.NOT_COMPLETE;
        }
        return HeaderState.COMPLETE;
    }

    public static Header parseHeader(String path) {
        Header header = null;
        RandomAccessFile raf = null;
        try {
            raf = RafHelper.createR(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (raf != null) {
                raf = RafHelper.createR(path);
                header = new HeaderTransform().read(raf);
                if (validateHeader(header) == HeaderState.INVALID) {
                    header = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RafHelper.close(raf);
        }
        return header;
    }
}
