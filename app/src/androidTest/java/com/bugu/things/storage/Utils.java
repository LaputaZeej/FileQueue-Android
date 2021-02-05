package com.bugu.things.storage;

import android.content.Context;
import android.util.Log;

import com.bugu.queue.bean._MqttMessage;
import com.bugu.things.storage.bean.MqttMessage;

import java.io.File;

import static com.bugu.things.storage.ext.ConstantKt.TEXT;

/**
 * Author by xpl, Date on 2021/1/29.
 */
public class Utils {

    public static String getPath(Context context, String fileName) {
        return context.getFilesDir().getAbsolutePath() + "/" + fileName;
    }

    public static void info(String tag, String msg) {
        Log.i(tag, msg == null ? "is null" : msg);
    }

    public static MqttMessage createMqttMessage(int index) {
        return new MqttMessage(
                System.currentTimeMillis(),
                index % 2,
                TEXT,
                index
        );
    }

    public static _MqttMessage.MqttMessage createProtobufMqttMessage(int index) {
        return _MqttMessage.MqttMessage.newBuilder().setContent(TEXT)
                .setId(index)
                .setType(index % 2)
                .setTime(System.currentTimeMillis())
                .build();
    }

    public static boolean delete(String path) {
        File file = new File(path);
        if (file.exists()) {
            boolean delete = file.delete();
            return delete;
        }
        return true;
    }
}
