package com.bugu.queue.converter;

import com.bugu.queue.FileQueue;
import com.bugu.queue.persistence.PersistenceRequest;
import com.bugu.queue.persistence.PersistenceResponse;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Nullable;

/**
 * Author by xpl, Date on 2021/2/7.
 */
public interface Converter<F, T> {
    T convert(F value) throws IOException;

    abstract class Factory {
        public @Nullable
        Converter<PersistenceResponse, ?> responseBodyConverter(
                Type type,
                Annotation[] annotations,
                FileQueue<?> fileQueue
        ) {
            return null;
        }

        public @Nullable
        Converter<?, PersistenceRequest> requestBodyConverter(
                Type type,
                Annotation[] parameterAnnotations,
                Annotation[] methodAnnotations,
                FileQueue<?> fileQueue) {
            return null;
        }
    }


}
