package com.bugu.queue;

import com.bugu.queue.header.Header;

/**
 * Author by xpl, Date on 2021/1/27.
 */
public interface OnFileQueueChanged {
    void onChanged(FileQueue<?> fileQueue, /*0:put 1:take*/int type, Header header);
}
