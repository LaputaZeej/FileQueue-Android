package com.bugu.queue.android;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Author by xpl, Date on 2021/1/29.
 */

@RunWith(AndroidJUnit4.class)
public class FileQueueTest {
    /**
     * 创建
     */
    @Test
    public void test01() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Looper.prepare();
        Looper.loop();
    }
}
