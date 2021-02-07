package com.bugu.queue.converter;

import com.bugu.queue.persistence.PersistenceResponse;
import com.bugu.queue.util.Logger;
import com.google.common.net.MediaType;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * Author by xpl, Date on 2021/2/7.
 */
public final class ProtobufResponseConverter<T extends MessageLite> implements Converter<PersistenceResponse, T> {
    private Parser<T> PARSER;

    private void createParse(Class<T> clz) {
        try {
            Field parser = clz.getDeclaredField("PARSER");
            parser.setAccessible(true);
            Object obj = parser.get(clz);
            PARSER = (Parser<T>) obj;
        } catch (Exception e) {
            e.printStackTrace();
            PARSER = new AbstractParser<T>() {
                @Override
                public T parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    try {
                        Constructor<T> constructor = clz.getDeclaredConstructor(CodedInputStream.class, ExtensionRegistryLite.class);
                        constructor.setAccessible(true);
                        T e = constructor.newInstance(input, extensionRegistry);
                        return e;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
        }
    }

    public ProtobufResponseConverter(Type type) {
        createParse((Class<T>) type);
    }

    @Override
    public T convert(PersistenceResponse value) throws IOException {
        Logger.info("ProtobufResponseConverter # convert");
        byte[] data = value.getData();
        try {
            return PARSER.parseFrom(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
