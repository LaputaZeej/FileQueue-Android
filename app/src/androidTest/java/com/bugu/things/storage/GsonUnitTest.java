//package com.bugu.things.storage;
//
//import android.Manifest;
//import android.content.Context;
//
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//import androidx.test.platform.app.InstrumentationRegistry;
//import androidx.test.rule.GrantPermissionRule;
//
//import com.bugu.queue.MutableFileQueue;
//import com.bugu.things.storage.bean.MqttMessage;
//import com.bugu.things.storage.ext.FileQueueExtKt;
//import com.bugu.things.storage.util.Utils;
//
//import org.jetbrains.annotations.NotNull;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.io.File;
//
//import static com.bugu.things.storage.Utils.createMqttMessage;
//import static com.bugu.things.storage.Utils.delete;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
///**
// * 基本的操作测试-Gsons数据
// * <p>
// * Author by xpl, Date on 2021/1/29.
// */
//@RunWith(AndroidJUnit4.class)
//public class GsonUnitTest {
//    private static final String TAG = "_GsonUnitTest_";
//
//    @Rule
//    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
//
//    /**
//     * 创建
//     */
//    @Test
//    public void test01() {
//        try {
//            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//            String path = com.bugu.things.storage.util.Utils.getPath(appContext, "/fileQueue/GsonUnitTest/test01.txt");
//            com.bugu.things.storage.util.Utils.info(TAG, path);
//            MutableFileQueue<MqttMessage> fileQueue = createFileQueue(appContext, path);
//            com.bugu.things.storage.util.Utils.info(TAG, "fileQueue = " + fileQueue);
//            assertNotNull(fileQueue);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 创建2
//     */
//    @Test
//    public void test02() {
//        try {
//            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//            String path = com.bugu.things.storage.util.Utils.getPath(appContext, "/fileQueue/GsonUnitTest/test02.txt");
//            com.bugu.things.storage.util.Utils.info(TAG, path);
//            MutableFileQueue<MqttMessage> fileQueue = createFileQueue(appContext, path);
//            com.bugu.things.storage.util.Utils.info(TAG, "fileQueue = " + fileQueue);
//            assertNotNull(fileQueue);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 存和取
//     */
//    @Test
//    public void test03() {
//        try {
//            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//            String path = com.bugu.things.storage.util.Utils.getPath(appContext, "/fileQueue/GsonUnitTest/test03.txt");
//            com.bugu.things.storage.util.Utils.info(TAG, path);
//            MutableFileQueue<MqttMessage> fileQueue = createFileQueue(appContext, path);
//            com.bugu.things.storage.util.Utils.info(TAG, "fileQueue = " + fileQueue);
//            assertNotNull(fileQueue);
//            MqttMessage mqttMessage = createMqttMessage(1);
//            fileQueue.put(mqttMessage);
//            com.bugu.things.storage.util.Utils.info(TAG, "put");
//            MqttMessage take = fileQueue.take();
//            com.bugu.things.storage.util.Utils.info(TAG, "take : " + take.toString());
//            assertNotNull(take);
//            assertEquals(mqttMessage.getContent(), take.getContent());
//            Thread.sleep(50000);
//            fileQueue.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 同时存和取
//     */
//    @Test
//    public void test04() {
//        try {
//            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//            String path = com.bugu.things.storage.util.Utils.getPath(appContext, "/fileQueue/GsonUnitTest/test04.txt");
//            delete(path);
//            com.bugu.things.storage.util.Utils.info(TAG, path);
//            MutableFileQueue<MqttMessage> fileQueue = createFileQueue(appContext, path);
//            new Thread(() -> {
//                for (int i = 0; i < 100; i++) {
//                    try {
//                        MqttMessage mqttMessage = createMqttMessage(i);
//                        fileQueue.put(mqttMessage);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                com.bugu.things.storage.util.Utils.info(TAG, "put1 end ");
//            }).start();
//
//            new Thread(() -> {
//                for (int i = 0; i < 100; i++) {
//                    try {
//                        Thread.sleep(200);
//                        MqttMessage mqttMessage = createMqttMessage(i);
//                        fileQueue.put(mqttMessage);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                com.bugu.things.storage.util.Utils.info(TAG, "put2 end ");
//            }).start();
//
//            new Thread(() -> {
//                MqttMessage take = null;
//                while (true) {
//                    try {
//                        Thread.sleep(100);
//                        take = fileQueue.take();
//                        com.bugu.things.storage.util.Utils.info(TAG, "take 1 : " + take.toString());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        break;
//                    }
//                }
//                com.bugu.things.storage.util.Utils.info(TAG, "take end ");
//            }).start();
//
//            new Thread(() -> {
//                MqttMessage take = null;
//                while (true) {
//                    try {
//                        Thread.sleep(100);
//                        take = fileQueue.take();
//                        com.bugu.things.storage.util.Utils.info(TAG, "take 2 : " + take.toString());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        break;
//                    }
//                }
//                com.bugu.things.storage.util.Utils.info(TAG, "take end ");
//            }).start();
//            Thread.sleep(60 * 1000);
//            fileQueue.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    /**
//     * 监听
//     */
//    @Test
//    public void test05() {
//        try {
//            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//            String path = com.bugu.things.storage.util.Utils.getPath(appContext, "/fileQueue/GsonUnitTest/test05.txt");
//            delete(path);
//            Utils.info(TAG, path);
//            MutableFileQueue<MqttMessage> fileQueue = createFileQueue(appContext, path);
//            fileQueue.setOnFileQueueChanged((fq, type, header) ->
//                    com.bugu.things.storage.util.Utils.info(TAG, FileQueueExtKt.logger(header, appContext, fileQueue.getMax()))
//            );
//            fileQueue.setOnFileQueueStateChanged((fq,state)-> com.bugu.things.storage.util.Utils.info(TAG,"state = "+state));
//            new Thread(() -> {
//                for (int i = 0; i < 100; i++) {
//                    try {
//                        MqttMessage mqttMessage = createMqttMessage(i);
//                        fileQueue.put(mqttMessage);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//
//            new Thread(() -> {
//                for (int i = 0; i < 100; i++) {
//                    try {
//                        Thread.sleep(200);
//                        MqttMessage mqttMessage = createMqttMessage(i);
//                        fileQueue.put(mqttMessage);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//
//            new Thread(() -> {
//                MqttMessage take = null;
//                while (true) {
//                    try {
//                        Thread.sleep(100);
//                        take = fileQueue.take();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        break;
//                    }
//                }
//            }).start();
//
//            new Thread(() -> {
//                MqttMessage take = null;
//                while (true) {
//                    try {
//                        Thread.sleep(100);
//                        take = fileQueue.take();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        break;
//                    }
//                }
//            }).start();
//            Thread.sleep(60 * 1000);
//            fileQueue.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 关闭
//     */
//    @Test
//    public void test06() {
//        try {
//            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//            String path = com.bugu.things.storage.util.Utils.getPath(appContext, "/fileQueue/GsonUnitTest/test06.txt");
//            delete(path);
//            com.bugu.things.storage.util.Utils.info(TAG, path);
//            MutableFileQueue<MqttMessage> fileQueue = createFileQueue(appContext, path);
//            fileQueue.setOnFileQueueChanged((fq, type, header) ->
//                    com.bugu.things.storage.util.Utils.info(TAG, FileQueueExtKt.logger(header, appContext, fileQueue.getMax()))
//            );
//            fileQueue.setOnFileQueueStateChanged((fq,state)-> com.bugu.things.storage.util.Utils.info(TAG,"state = "+state));
//            new Thread(() -> {
//                for (int i = 0; i < 100; i++) {
//                    try {
//                        MqttMessage mqttMessage = createMqttMessage(i);
//                        fileQueue.put(mqttMessage);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//
//            new Thread(() -> {
//                for (int i = 0; i < 100; i++) {
//                    try {
//                        Thread.sleep(200);
//                        MqttMessage mqttMessage = createMqttMessage(i);
//                        fileQueue.put(mqttMessage);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//
//            new Thread(() -> {
//                MqttMessage take = null;
//                while (true) {
//                    try {
//                        Thread.sleep(100);
//                        take = fileQueue.take();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        break;
//                    }
//                }
//            }).start();
//
//            new Thread(() -> {
//                MqttMessage take = null;
//                while (true) {
//                    try {
//                        Thread.sleep(100);
//                        take = fileQueue.take();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        break;
//                    }
//                }
//            }).start();
//            Thread.sleep(5 * 1000);
//            com.bugu.things.storage.util.Utils.info(TAG, "5秒后关闭...");
//            fileQueue.close();
//            assertTrue(fileQueue.isClosed());
//            Thread.sleep(60 * 1000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 删除
//     */
//    @Test
//    public void test07() {
//        try {
//            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//            String path = com.bugu.things.storage.util.Utils.getPath(appContext, "/fileQueue/GsonUnitTest/test07.txt");
//            delete(path);
//            com.bugu.things.storage.util.Utils.info(TAG, path);
//            MutableFileQueue<MqttMessage> fileQueue = createFileQueue(appContext, path);
//            fileQueue.setOnFileQueueChanged((fq, type, header) ->
//                    com.bugu.things.storage.util.Utils.info(TAG, FileQueueExtKt.logger(header, appContext, fileQueue.getMax()))
//            );
//            fileQueue.setOnFileQueueStateChanged((fq,state)-> com.bugu.things.storage.util.Utils.info(TAG,"state = "+state));
//            new Thread(() -> {
//                for (int i = 0; i < 100; i++) {
//                    try {
//                        MqttMessage mqttMessage = createMqttMessage(i);
//                        fileQueue.put(mqttMessage);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//
//            new Thread(() -> {
//                for (int i = 0; i < 100; i++) {
//                    try {
//                        Thread.sleep(200);
//                        MqttMessage mqttMessage = createMqttMessage(i);
//                        fileQueue.put(mqttMessage);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//
//            new Thread(() -> {
//                MqttMessage take = null;
//                while (true) {
//                    try {
//                        Thread.sleep(100);
//                        take = fileQueue.take();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        break;
//                    }
//                }
//            }).start();
//
//            new Thread(() -> {
//                MqttMessage take = null;
//                while (true) {
//                    try {
//                        Thread.sleep(100);
//                        take = fileQueue.take();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        break;
//                    }
//                }
//            }).start();
//            Thread.sleep(5 * 1000);
//            Utils.info(TAG, "5秒后删除...");
//            fileQueue.delete();
//            assertTrue(fileQueue.isClosed());
//            assertFalse(new File(path).exists());
//            Thread.sleep(60 * 1000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @NotNull
//    private MutableFileQueue<MqttMessage> createFileQueue(Context appContext, String path) {
//        return new MutableFileQueue.Builder().path(path).build();
//    }
//
//
//}
//
//
