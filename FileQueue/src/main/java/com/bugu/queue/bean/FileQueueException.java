package com.bugu.queue.bean;

/**
 * Author by xpl, Date on 2021/1/27.
 */
public class FileQueueException extends Exception{
    public FileQueueException() {
    }

    public FileQueueException(String message) {
        super(message);
    }

    public FileQueueException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileQueueException(Throwable cause) {
        super(cause);
    }

    public FileQueueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
