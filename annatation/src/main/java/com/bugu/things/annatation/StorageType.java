package com.bugu.things.annatation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface StorageType {
    String id();

    Class<?> type();

    String path();

    int mode() default 0;

    long cap() default 0L;

    long max() default 0L;
}