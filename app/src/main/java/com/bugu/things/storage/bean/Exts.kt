package com.bugu.things.storage.bean

import com.bugu.queue.bean._MqttMessage

/**
 * Author by xpl, Date on 2021/1/28.
 */

fun _MqttMessage.MqttMessage.print() =   ("id = $id ,content = $content type = $type , time = $time")