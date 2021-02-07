package com.bugu.queue.converter;

import com.bugu.queue.FileQueue;
import com.bugu.queue.persistence.PersistenceRequest;
import com.bugu.queue.persistence.PersistenceResponse;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Nullable;

/**
 * Author by xpl, Date on 2021/2/7.
 */
public class GsonConverterFactory extends Converter.Factory {
    private final Gson gson;

    private GsonConverterFactory(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
    }

    public static GsonConverterFactory create() {
        return new GsonConverterFactory(new Gson());
    }

    @Nullable
    @Override
    public Converter<PersistenceResponse, ?> responseBodyConverter(
            Type type,
            Annotation[] annotations,
            FileQueue<?> fileQueue) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonResponseConverter<>(gson, adapter);
    }

    @Nullable
    @Override
    public Converter<?, PersistenceRequest> requestBodyConverter(
            Type type,
            Annotation[] parameterAnnotations,
            Annotation[] methodAnnotations, FileQueue<?> fileQueue) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonRequestConverter<>(gson, adapter);
    }
}
