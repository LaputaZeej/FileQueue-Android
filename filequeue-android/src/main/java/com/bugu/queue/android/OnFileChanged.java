package com.bugu.queue.android;

/**
 * Author by xpl, Date on 2021/1/25.
 */
public interface OnFileChanged {
    void onChanged(AndroidFileQueue<?> fileQueue, String logger);

    void onStateChanged(AndroidFileQueue<?> fileQueue,boolean full);
}
