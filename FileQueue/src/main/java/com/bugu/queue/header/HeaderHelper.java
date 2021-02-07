package com.bugu.queue.header;

import com.bugu.queue.Version;

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
}
