package com.bugu.things.storage

import com.bugu.things.storage.bean.MqttMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun t1() {
//        val assignableFrom =
//            MessageLite::class.java.isAssignableFrom(_MqttMessage.MqttMessage::class.java)
//        println("as = $assignableFrom")
        val s: String? = null
        val format = String.format("haha->%s", "$s")
        val java = java.lang.String.format("heihei->%s", "$s")
        println(format)
        println(java)

    }
}