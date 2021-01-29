package com.bugu.queue.transform;

import com.bugu.queue.util.Logger;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;

import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

import sun.rmi.runtime.Log;

/**
 * Author by xpl, Date on 2021/1/27.
 */
public class ProtobufTransform<E extends MessageLite> extends AbsTransform<E> {
    private Parser<E> PARSER;

    public ProtobufTransform(Class<E> clz) {
        try {
            Field parser = clz.getDeclaredField("PARSER");
            parser.setAccessible(true);
            Object obj = parser.get(clz);
            PARSER = (Parser<E>) obj;
        } catch (Exception e) {
            e.printStackTrace();
            PARSER = createParse(clz);
        }
    }

    private Parser<E> createParse(Class<E> clz) {
        return new AbstractParser<E>() {
            @Override
            public E parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                try {
                    Constructor<E> constructor = clz.getDeclaredConstructor(CodedInputStream.class, ExtensionRegistryLite.class);
                    constructor.setAccessible(true);
                    E e = constructor.newInstance(input, extensionRegistry);
                    return e;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }


    @Override
    public void write(E e, RandomAccessFile raf) throws Exception {
        byte[] bytes = e.toByteArray();
        write0(raf, bytes);
    }

    @Override
    public E read(RandomAccessFile raf) throws Exception {
        E e = null;
        try {
            byte[] read = read0(raf);
            e = PARSER.parseFrom(read);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return e;
    }
}
