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
//import com.bugu.queue.bean.GenericityEntity;
//import com.bugu.queue.bean._MqttMessage;
//import com.bugu.queue.transform.ProtobufTransform;
//import com.bugu.queue.util.Size;
//import com.bugu.things.storage.ext.ExtsKt;
//import com.bugu.things.storage.ext.FileQueueExtKt;
//import com.bugu.things.storage.util.Utils;
//import com.google.protobuf.Any;
//
//import org.jetbrains.annotations.NotNull;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.io.File;
//
//import static com.bugu.things.storage.util.Utils.createProtobufMqttMessage;
//import static com.bugu.things.storage.util.Utils.delete;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
///**
// * 基本的操作测试 - protobuf数据
// * <p>
// * Author by xpl, Date on 2021/1/29.
// */
//@RunWith(AndroidJUnit4.class)
//public class ProtobufUnitTest {
//    private static final String TAG = "_ProtobufUnitTest_";
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
//            String path = com.bugu.things.storage.util.Utils.getPath(appContext, "/fileQueue/ProtobufUnitTest/test01.txt");
//            com.bugu.things.storage.util.Utils.info(TAG, path);
//            MutableFileQueue<_MqttMessage.MqttMessage> fileQueue = createFileQueue(appContext, path);
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
//            String path = com.bugu.things.storage.util.Utils.getPath(appContext, "/fileQueue/ProtobufUnitTest/test02.txt");
//            com.bugu.things.storage.util.Utils.info(TAG, path);
//            MutableFileQueue<_MqttMessage.MqttMessage> fileQueue = createFileQueue(appContext, path);
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
//            String path = com.bugu.things.storage.util.Utils.getPath(appContext, "/fileQueue/ProtobufUnitTest/test03.txt");
//            com.bugu.things.storage.util.Utils.info(TAG, path);
//            MutableFileQueue<_MqttMessage.MqttMessage> fileQueue = createFileQueue(appContext, path);
//            com.bugu.things.storage.util.Utils.info(TAG, "fileQueue = " + fileQueue);
//            assertNotNull(fileQueue);
//            _MqttMessage.MqttMessage mqttMessage = createProtobufMqttMessage(1);
//            fileQueue.put(mqttMessage);
//            com.bugu.things.storage.util.Utils.info(TAG, "put");
//            _MqttMessage.MqttMessage take = fileQueue.take();
//            com.bugu.things.storage.util.Utils.info(TAG, "take : " + ExtsKt.print(take));
//            assertNotNull(take);
//            assertEquals(mqttMessage.getContent(), take.getContent());
//            fileQueue.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 存和取 protobuf 泛型
//     */
//    @Test
//    public void test0301() {
//        try {
//            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//            _MqttMessage.MqttMessage mqttMessage = createProtobufMqttMessage(1);
//            GenericityEntity.FileQueueMessage msg = GenericityEntity.FileQueueMessage.newBuilder().setData(Any.pack(mqttMessage)).build();
//            String path = com.bugu.things.storage.util.Utils.getPath(appContext, "/fileQueue/ProtobufUnitTest/test0301.txt");
//            MutableFileQueue<GenericityEntity.FileQueueMessage> fileQueue =
//                    new MutableFileQueue.Builder<GenericityEntity.FileQueueMessage>()
//                            .path(path)
//                            .transform(new ProtobufTransform<>(GenericityEntity.FileQueueMessage.class))
//                            .build();
//            com.bugu.things.storage.util.Utils.info(TAG, "put start ");
//            fileQueue.put(msg);
//            com.bugu.things.storage.util.Utils.info(TAG, "put end ");
//            GenericityEntity.FileQueueMessage take = fileQueue.take();
//            com.bugu.things.storage.util.Utils.info(TAG, "take end");
//            Any data = take.getData();
//            _MqttMessage.MqttMessage unpack = data.unpack(_MqttMessage.MqttMessage.class);
//            com.bugu.things.storage.util.Utils.info(TAG, unpack.getContent());
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
//            String path = com.bugu.things.storage.util.Utils.getPath(appContext, "/fileQueue/ProtobufUnitTest/test04.txt");
//            delete(path);
//            com.bugu.things.storage.util.Utils.info(TAG, path);
//            MutableFileQueue<_MqttMessage.MqttMessage> fileQueue = createFileQueue(appContext, path);
//            new Thread(() -> {
//                for (int i = 0; i < 100; i++) {
//                    try {
//                        _MqttMessage.MqttMessage mqttMessage = createProtobufMqttMessage(i);
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
//                        _MqttMessage.MqttMessage mqttMessage = createProtobufMqttMessage(i);
//                        fileQueue.put(mqttMessage);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                com.bugu.things.storage.util.Utils.info(TAG, "put2 end ");
//            }).start();
//
//            new Thread(() -> {
//                _MqttMessage.MqttMessage take = null;
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
//                _MqttMessage.MqttMessage take = null;
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
//            String path = com.bugu.things.storage.util.Utils.getPath(appContext, "/fileQueue/ProtobufUnitTest/test05.txt");
//            delete(path);
//            com.bugu.things.storage.util.Utils.info(TAG, path);
//            MutableFileQueue<_MqttMessage.MqttMessage> fileQueue = createFileQueue(appContext, path);
//            fileQueue.setOnFileQueueChanged((fq, type, header) ->
//                    com.bugu.things.storage.util.Utils.info(TAG, FileQueueExtKt.logger(header, appContext, fileQueue.getMax()))
//            );
//            fileQueue.setOnFileQueueStateChanged((fq,state)-> com.bugu.things.storage.util.Utils.info(TAG,"state = "+state));
//            new Thread(() -> {
//                for (int i = 0; i < 100; i++) {
//                    try {
//                        _MqttMessage.MqttMessage mqttMessage = createProtobufMqttMessage(i);
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
//                        _MqttMessage.MqttMessage mqttMessage = createProtobufMqttMessage(i);
//                        fileQueue.put(mqttMessage);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//
//            new Thread(() -> {
//                _MqttMessage.MqttMessage take = null;
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
//                _MqttMessage.MqttMessage take = null;
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
//            String path = com.bugu.things.storage.util.Utils.getPath(appContext, "/fileQueue/ProtobufUnitTest/test06.txt");
//            delete(path);
//            com.bugu.things.storage.util.Utils.info(TAG, path);
//            MutableFileQueue<_MqttMessage.MqttMessage> fileQueue = createFileQueue(appContext, path);
//            fileQueue.setOnFileQueueChanged((fq, type, header) ->
//                    com.bugu.things.storage.util.Utils.info(TAG, FileQueueExtKt.logger(header, appContext, fileQueue.getMax()))
//            );
//            fileQueue.setOnFileQueueStateChanged((fq,state)-> com.bugu.things.storage.util.Utils.info(TAG,"state = "+state));
//            new Thread(() -> {
//                for (int i = 0; i < 100; i++) {
//                    try {
//                        _MqttMessage.MqttMessage mqttMessage = createProtobufMqttMessage(i);
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
//                        _MqttMessage.MqttMessage mqttMessage = createProtobufMqttMessage(i);
//                        fileQueue.put(mqttMessage);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//
//            new Thread(() -> {
//                _MqttMessage.MqttMessage take = null;
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
//                _MqttMessage.MqttMessage take = null;
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
//            String path = com.bugu.things.storage.util.Utils.getPath(appContext, "/fileQueue/ProtobufUnitTest/test07.txt");
//            delete(path);
//            com.bugu.things.storage.util.Utils.info(TAG, path);
//            MutableFileQueue<_MqttMessage.MqttMessage> fileQueue = createFileQueue(appContext, path);
//            fileQueue.setOnFileQueueChanged((fq, type, header) ->
//                    com.bugu.things.storage.util.Utils.info(TAG, FileQueueExtKt.logger(header, appContext, fileQueue.getMax()))
//            );
//            fileQueue.setOnFileQueueStateChanged((fq,state)-> com.bugu.things.storage.util.Utils.info(TAG,"state = "+state));
//            new Thread(() -> {
//                for (int i = 0; i < 100; i++) {
//                    try {
//                        _MqttMessage.MqttMessage mqttMessage = createProtobufMqttMessage(i);
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
//                        _MqttMessage.MqttMessage mqttMessage = createProtobufMqttMessage(i);
//                        fileQueue.put(mqttMessage);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//
//            new Thread(() -> {
//                _MqttMessage.MqttMessage take = null;
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
//                _MqttMessage.MqttMessage take = null;
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
//    private MutableFileQueue<_MqttMessage.MqttMessage> createFileQueue(Context appContext, String path) {
//        return new MutableFileQueue.Builder<_MqttMessage.MqttMessage>().path(path)
//                .transform(new ProtobufTransform<>(_MqttMessage.MqttMessage.class))
//                .maxSize(Size._G)
//                .build();
//    }
//
//
//}
