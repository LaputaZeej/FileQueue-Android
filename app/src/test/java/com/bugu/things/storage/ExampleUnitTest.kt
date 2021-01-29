package com.bugu.things.storage

import com.bugu.queue.bean._MqttMessage
import com.google.protobuf.MessageLite
import kotlinx.coroutines.GlobalScope
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
    fun addition_isCorrect() {
        runBlocking {
            try {
                launch {
                    val channel: Channel<String> = Channel(/*取最新的*/Channel.CONFLATED)
                    var indexA = 0
                    launch {


                        try {
                            while (true) {
                                delay(3000)
                                channel.send("[A] $indexA")
                                indexA++
                            }
                        } catch (e: Throwable) {
                            println(" send a $e")
                        }
                    }
                    var indexB = 0
                    launch {
                        try {
                            while (true) {
                                delay(1000)
                                channel.send("[B] $indexB")
                                indexB++
                            }
                        } catch (e: Throwable) {
                            println(" send b $e")
                        }

                    }
                    launch {
                        try {
                            for (i in channel) {
                                println("receiver x = $i")
                            }
                        } catch (e: Throwable) {
                            println(" for $e")
                        }
                    }
                    launch {
                        try {
                            while (true) {
                                println("receiver y = ${channel.receive()}")
                            }
                        } catch (e: Throwable) {
                            println(" while $e")
                        }
                    }
                    delay(10 * 1000)
                    channel.close()
//                    channel.cancel()
                }
            } catch (e: Throwable) {
                println(" channel $e")
            }
        }
    }

    @Test
    fun t1() {
        val assignableFrom =
            MessageLite::class.java.isAssignableFrom(_MqttMessage.MqttMessage::class.java)
        println("as = $assignableFrom")
    }
}