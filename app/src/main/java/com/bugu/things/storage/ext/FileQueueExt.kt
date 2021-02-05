package com.bugu.things.storage.ext

import android.content.Context
import android.text.format.Formatter
import com.bugu.queue.MutableFileQueue
import com.bugu.queue.header.Header
import com.bugu.queue.transform.GsonTransform
import com.bugu.queue.transform.ProtobufTransform
import com.bugu.queue.util.Size
import com.google.gson.reflect.TypeToken
import com.google.protobuf.MessageLite
import java.util.*
import kotlin.math.log

/**
 * Author by xpl, Date on 2021/2/3.
 */


inline fun <reified T : Any> Context.createGsonFileQueue(path: String): MutableFileQueue<T> {
    val type = object : TypeToken<T>() {}.type
    return MutableFileQueue.Builder<T>()
        .path(path)
        .maxSize(Size._G * 3)
        .transform(GsonTransform<T>(type))
        .build()
}

inline fun <reified T : MessageLite> Context.createProtoFileQueue(path: String): MutableFileQueue<T> =
    MutableFileQueue.Builder<T>()
        .maxSize(Size._G * 3)
        .path(path)
        .transform(ProtobufTransform<T>(T::class.java))
        .build()

fun Header.logger(context: Context, max: Long) = this.run {
    val head: Long = this.getHead()
    val tail: Long = this.getTail()
    val length: Long = this.getLength()
    val formatLength = Formatter.formatFileSize(
        context,
        this.length
    )
    val formatMaxSize =
        Formatter.formatFileSize(context, max)
    val ratio =
        String.format(Locale.getDefault(), "%d%%", length * 100 / max)
    val logger =
        "[head = $head tail = $tail]$formatLength/$formatMaxSize[$ratio]"
    logger
}
