package com.bugu.queue;

/**
 * Author by xpl, Date on 2021/1/27.
 */
public interface OnFileQueueStateChanged {
    void onChanged(FileQueue<?> fileQueue, int state);
}
