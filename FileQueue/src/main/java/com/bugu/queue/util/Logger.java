package com.bugu.queue.util;

/**
 * Author by xpl, Date on 2021/1/27.
 */
public class Logger {
    private static final String TAG = "[FileQueue] ";
    private static boolean debug = true;

    public static void info(String msg) {
        if (debug) {
            System.out.println(TAG + msg);
        }
    }

    public static void warning(String msg) {
        if (debug) {
            System.out.println(TAG + msg);
        }
    }
}
