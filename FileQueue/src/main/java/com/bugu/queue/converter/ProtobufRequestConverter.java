package com.bugu.queue.converter;

import com.bugu.queue.persistence.PersistenceRequest;
import com.bugu.queue.util.Logger;
import com.google.common.net.MediaType;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;
import com.google.protobuf.MessageLite;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import okio.Buffer;

/**
 * Author by xpl, Date on 2021/2/7.
 */
public final class ProtobufRequestConverter<T extends MessageLite> implements Converter<T, PersistenceRequest> {

    public ProtobufRequestConverter() {
    }

    @Override
    public PersistenceRequest convert(T value) throws IOException {
        Logger.info("ProtobufRequestConverter # convert");
        return new PersistenceRequest(value.toByteArray());
    }
}
