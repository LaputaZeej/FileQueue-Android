package com.bugu.queue;

import com.bugu.queue.header.Header;

/**
 * Author by xpl, Date on 2021/1/27.
 */
public interface OnFileQueueChanged {
    void onChanged(FileQueue<?> fileQueue, int type, Header header);
}
