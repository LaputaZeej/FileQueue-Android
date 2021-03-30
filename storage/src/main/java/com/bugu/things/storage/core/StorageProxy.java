package com.bugu.things.storage.core;

import android.util.Log;

import com.bugu.queue.FileQueue;
import com.bugu.queue.MutableFileQueue;
import com.bugu.queue.converter.GsonConverterFactory;
import com.bugu.things.storage.bean.Delete;
import com.bugu.things.storage.bean.Put;
import com.bugu.things.storage.bean.Take;
import com.bugu.things.storage.util.Utils;
import com.google.gson.Gson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author by xpl, Date on 2021/2/8.
 */
public class StorageProxy {
    private Gson gson = new Gson();
    private Map<String, FileQueue<?>> mFileQueues = new ConcurrentHashMap<>();
    private Map<String, Class<?>> mClasses = new ConcurrentHashMap<>();

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public StorageProxy() {
    }


    public <T> T create(Class<T> clz) {
        Utils.validateServiceInterface(clz);
        // TODO 加载缓存
        Object obj = Proxy.newProxyInstance(clz.getClassLoader(),
                new Class[]{clz},
                (proxy, method, args) -> {
                    // If the method is a method from Object then defer to normal invocation.
                    if (method.getDeclaringClass() == Object.class) {
                        return method.invoke(this, args);
                    }
                    // 解析注解，构造fileQueue，执行take put delete
                    FileQueue<Object> fileQueue;
                    info("method ->" + method.getName());
                    if (method.getAnnotation(Put.class) != null) {
                        Put annotation = method.getAnnotation(Put.class);
                        String name = annotation.name();
                        String fileName = path + name;
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        for (int i = 0; i < parameterTypes.length; i++) {
                            if (parameterTypes[i]!=null){
                                if (parameterTypes[i]==args[i].getClass()) {
                                    Class<?> aClass = args[i].getClass();
                                    fileQueue = (FileQueue<Object>) getOrCreateFileQueue(aClass, fileName);
                                    Object arg = args[i];
                                    fileQueue.put(arg);
                                    break;
                                }
                            }
                        }
                        return true;
                    } else if (method.getAnnotation(Take.class) != null) {
                        Class<?> returnType = method.getReturnType();
                        Take annotation = method.getAnnotation(Take.class);
                        String name = annotation.name();
                        String fileName = path + name;
                        fileQueue = (FileQueue<Object>) getOrCreateFileQueue(returnType, fileName);
                        Object take = fileQueue.take();
                        return take;
                    } else if (method.getAnnotation(Delete.class) != null) {
                        Class<?> returnType = method.getReturnType();
                        Delete annotation = method.getAnnotation(Delete.class);
                        String name = annotation.name();
                        String fileName = path + name;
                        fileQueue = (FileQueue<Object>) getOrCreateFileQueue(null, fileName);
                        fileQueue.delete();
                        return true;
                    }
                    return null;
                });
        return (T) obj;
    }

    private FileQueue<?> getOrCreateFileQueue(Class<?> clz, String fileName) {
        FileQueue<?> fileQueue = mFileQueues.get(fileName);
        if (fileQueue == null) {
            fileQueue = new MutableFileQueue.Builder()
                    .capacity(0)
                    .path(fileName)
                    .maxSize(0)
                    .factory(GsonConverterFactory.create())
                    .type(clz)
                    .build();
            mFileQueues.put(fileName, fileQueue);
        }
        return fileQueue;
    }

    private void info(String msg) {
        Log.i("laputa", msg);
    }

//    public static StorageProxy getInstance() {
//        return Holder.INSTANCE;
//    }
//
//    private static final class Holder {
//        private static final StorageProxy INSTANCE = new StorageProxy();
//    }
}
