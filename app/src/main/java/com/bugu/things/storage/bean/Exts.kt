package com.bugu.things.storage.bean

import android.content.Context
import com.bugu.queue.bean._MqttMessage

/**
 * Author by xpl, Date on 2021/1/28.
 */

fun _MqttMessage.MqttMessage.print() =   ("id = $id ,content = $content type = $type , time = $time")

fun getPath(context: Context, fileName: String): String {
    return context.filesDir.absolutePath + "/" + fileName
}