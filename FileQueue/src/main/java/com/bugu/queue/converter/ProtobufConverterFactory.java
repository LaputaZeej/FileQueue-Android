package com.bugu.queue.converter;

import com.bugu.queue.FileQueue;
import com.bugu.queue.persistence.PersistenceRequest;
import com.bugu.queue.persistence.PersistenceResponse;
import com.google.gson.Gson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Nullable;

/**
 * Author by xpl, Date on 2021/2/7.
 */
public class ProtobufConverterFactory extends Converter.Factory {

    private ProtobufConverterFactory() {
    }

    public static ProtobufConverterFactory create() {
        return new ProtobufConverterFactory();
    }

    @Nullable
    @Override
    public Converter<PersistenceResponse, ?> responseBodyConverter(
            Type type,
            Annotation[] annotations,
            FileQueue<?> fileQueue) {
        return new ProtobufResponseConverter<>( type);
    }

    @Nullable
    @Override
    public Converter<?, PersistenceRequest> requestBodyConverter(
            Type type,
            Annotation[] parameterAnnotations,
            Annotation[] methodAnnotations, FileQueue<?> fileQueue) {
        return new ProtobufRequestConverter<>();
    }
}
