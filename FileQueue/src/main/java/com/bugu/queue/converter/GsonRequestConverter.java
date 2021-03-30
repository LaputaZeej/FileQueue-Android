package com.bugu.queue.converter;

import com.bugu.queue.persistence.PersistenceRequest;
import com.bugu.queue.util.Logger;
import com.google.common.net.MediaType;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import okio.Buffer;

/**
 * Author by xpl, Date on 2021/2/7.
 */
public final class GsonRequestConverter<T> implements Converter<T, PersistenceRequest> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    private final Gson gson;
    private final TypeAdapter<T> adapter;

    public GsonRequestConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override public PersistenceRequest convert(T value) throws IOException {
        //Logger.info("GsonRequestConverter # convert");
        Buffer buffer = new Buffer();
        Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
        JsonWriter jsonWriter = gson.newJsonWriter(writer);
        adapter.write(jsonWriter, value);
        jsonWriter.close();
        return new PersistenceRequest(buffer.readByteArray());
    }
}
