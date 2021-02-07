//package com.bugu.things.storage;
//
//import com.bugu.things.annatation.StorageType;
//import com.google.gson.reflect.TypeToken;
//
//import java.lang.reflect.Type;
//import java.util.List;
//
///**
// * Author by xpl, Date on 2021/2/5.
// */
//@StorageType(clz = String.class, path = "xx")
//public interface DemoStorage {
//    default Type type() {
//        return new TypeToken<List<String>>() {
//        }.getType();
//    }
//
//    default String rootPath() {
//        return "";
//    }
//}
