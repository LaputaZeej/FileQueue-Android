package com.bugu.queue.converter;

import com.bugu.queue.persistence.PersistenceResponse;
import com.bugu.queue.util.Logger;
import com.google.common.net.MediaType;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * Author by xpl, Date on 2021/2/7.
 */
public final class GsonResponseConverter<T> implements Converter<PersistenceResponse, T> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    public GsonResponseConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(PersistenceResponse value) throws IOException {
        //Logger.info("GsonResponseConverter # convert");
        Reader r = new InputStreamReader(new ByteArrayInputStream(value.getData()),UTF_8);
        try (JsonReader jsonReader = gson.newJsonReader(r)) {
            return adapter.read(jsonReader);
        }
    }
}
