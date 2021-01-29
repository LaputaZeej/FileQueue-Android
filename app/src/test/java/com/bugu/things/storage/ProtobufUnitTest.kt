package com.bugu.things.storage

import com.bugu.queue.bean._MqttMessage
import com.bugu.things.storage.bean.MqttMessage
import com.bugu.things.storage.bean.TEXT
import com.bugu.things.storage.bean.TEXT_01
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.protobuf.Parser
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.RandomAccessFile
import java.lang.reflect.Field
import java.nio.charset.Charset

/**
 * Author by xpl, Date on 2021/1/28.
 */
class ProtobufUnitTest {

    @Test
    fun t01() {
        val message = _MqttMessage.MqttMessage.newBuilder()
            .setContent("abc123危楼高百尺,手可摘星辰。不敢高声语，唯恐天上人！456xyz")
            .setId(1)
            .setTime(System.currentTimeMillis())
            .setType(0)
            .build()
        val toByteString = message.toByteString()
        println("isEmpty : ${toByteString.isEmpty}")
        println("isValidUtf8 ${toByteString.isValidUtf8}")
        println("size ${toByteString.size()}")
        println("toString ${toByteString.toString(Charset.forName("ISO-8859-1"))}")
        println("toString ${toByteString.toString(Charset.forName("utf-8"))}")
        println("toString ${toByteString.toString(Charset.forName("UTF-8"))}")
        println("toString ${toByteString.toStringUtf8()}")
    }

    @Test
    fun t02() {
        val message = _MqttMessage.MqttMessage.newBuilder()
            .setContent("abc123危楼高百尺,手可摘星辰。不敢高声语，唯恐天上人！456xyz")
            .setId(1)
            .setTime(System.currentTimeMillis())
            .setType(0)
            .build()
        val toByteArray = message.toByteArray()
        println("size : ${toByteArray.size}")
        println("toString : ${toByteArray.toString()}")

        val PARSER = getParser()
        val parseFrom = PARSER.parseFrom(toByteArray)
        parseFrom.print()
    }

    private fun _MqttMessage.MqttMessage.print() {
        println("id = $id ,content = $content type = $type , time = $time")
    }

    @Test
    fun t03() {
        // 怎么从连续的流中读出对象？
        val PARSER = getParser()
        val message = _MqttMessage.MqttMessage.newBuilder()
            .setContent("abc123危楼高百尺,手可摘星辰。不敢高声语，唯恐天上人！456xyz")
            .setId(1)
            .setTime(System.currentTimeMillis())
            .setType(0)
            .build()
        // T:MessageLite -> byte[] -> MessageLite
        val data = message.toByteArray()
        println("size : ${data.size}")
        PARSER.parseFrom(data).print()

        // 用RandomAccessFile.writeUTF
        // T:MessageLite -> byte[] -> String -> byte[] -> T:MessageLite
        val strData = String(data, Charset.forName("ISO-8859-1"))
        val dataNew = strData.toByteArray(Charset.forName("ISO-8859-1"))
        println("strData : ${strData}")
        println("size : ${dataNew.size}")
        // error com.google.protobuf.InvalidProtocolBufferException: CodedInputStream encountered a malformed varint.
        PARSER.parseFrom(dataNew).print()


    }

    @Test
    fun t04() {
        // 怎么从连续的流中读出对象？
        val PARSER = getParser()
        val message = _MqttMessage.MqttMessage.newBuilder()
            .setContent("abc123 $TEXT 456xyz")
            .setId(1)
            .setTime(System.currentTimeMillis())
            .setType(0)
            .build()
        // T:MessageLite -> byte[] -> MessageLite
        val data = message.toByteArray()
        println("size : ${data.size}")
        PARSER.parseFrom(data).print()

        // 用RandomAccessFile.writeUTF
        // T:MessageLite -> byte[] -> String -> byte[] -> T:MessageLite
        val strData = String(data, Charset.forName("ISO-8859-1"))
        val dataNew = strData.toByteArray(Charset.forName("ISO-8859-1"))
        //println("strData : ${strData}")
        println("size : ${dataNew.size}")
        // error com.google.protobuf.InvalidProtocolBufferException: CodedInputStream encountered a malformed varint.
        PARSER.parseFrom(dataNew).print()


    }

    @Test
    fun t05() {
        // 怎么从连续的流中读出对象？
        val PARSER = getParser()
        val message = _MqttMessage.MqttMessage.newBuilder()
            .setContent("abc123 $TEXT_01 456xyz")
            .setId(1)
            .setTime(System.currentTimeMillis())
            .setType(0)
            .build()
        // T:MessageLite -> byte[] -> String -> byte[] -> T:MessageLite
        val randomAccessFile = RandomAccessFile(createPath("android2"), "rw")
        println("写。。。。。。。。。。。。。")
        val data = message.toByteArray()
        println("size : ${data.size}")
        val strData = String(data, Charset.forName("ISO-8859-1"))
        println("strData.size : ${strData.length}")
        randomAccessFile.writeUTF(strData)
        Thread.sleep(3000)
        println("读。。。。。。。。。。。。。")
        randomAccessFile.seek(0)
        val dataNew = randomAccessFile.readUTF().toByteArray(Charset.forName("ISO-8859-1"))
        println("size : ${dataNew.size}")
        PARSER.parseFrom(dataNew).print()


    }

    @Test
    fun t06() {
        val PARSER = getParser()
        val message = _MqttMessage.MqttMessage.newBuilder()
            .setContent("abc123 $TEXT_01 456xyz")
            .setId(1)
            .setTime(System.currentTimeMillis())
            .setType(0)
            .build()
        // T:MessageLite -> byte[] -> String -> byte[] -> T:MessageLite
        val randomAccessFile = RandomAccessFile(createPath("android3"), "rw")
        val gson: Gson = Gson()
        println("写。。。。。。。。。。。。。")
        val strData = gson.toJson(message)
        println("strData.size : ${strData.length}")
        println("strData.size : ${strData.toByteArray(Charset.forName("ISO-8859-1")).size}")
        randomAccessFile.writeUTF(strData)
        Thread.sleep(3000)
        println("读。。。。。。。。。。。。。")
        randomAccessFile.seek(0)
        val dataNew = randomAccessFile.readUTF()
        println("size : ${dataNew.length}")
        gson.fromJson(dataNew, _MqttMessage.MqttMessage::class.java).print()


    }

    @Test
    fun t07() {
        println("Charset.defaultCharset()) = ${Charset.defaultCharset()}")
        val message = _MqttMessage.MqttMessage.newBuilder()
            .setContent("abc123 $TEXT 456xyz")
            .setId(1)
            .setTime(System.currentTimeMillis())
            .setType(0)
            .build()
        val gson: Gson = Gson()
        val json = gson.toJson(message)
        println("json 1= ${json.length}") // 12506
        println("json 2= ${json.toByteArray(Charset.forName("ISO-8859-1")).size}") // 12506
        println("json 3= ${json.toByteArray(Charset.forName("UTF-8")).size}") // 35290
        val protobuf = message.toByteArray()
        println("protobuf = ${protobuf.size}") // 35035
        println("protobuf = ${message.toString().length}") // 35035
    }

    private fun getParser(): Parser<_MqttMessage.MqttMessage> {
        val clz = _MqttMessage.MqttMessage::class.java
        val parser: Field = clz.getDeclaredField("PARSER")
        parser.setAccessible(true)
        val obj: Any = parser.get(clz)
        val PARSER = obj as Parser<_MqttMessage.MqttMessage>
        return PARSER
    }

    private fun createPath(fileName: String): String? {
        return "C:\\Users\\xpl\\Documents\\projs\\mqtt\\case\\$fileName"
    }

    @Test
    fun t08() {
        val index = 1
        val message = MqttMessage(
            System.currentTimeMillis(),
            (index % 2).toInt(),
            "t->$index $TEXT",
            index.toLong(),
            "DESC $TEXT",
            "TITLE $TEXT",
            "NAME $TEXT"
        )
        // T:MessageLite -> byte[] -> String -> byte[] -> T:MessageLite
        val randomAccessFile = RandomAccessFile(createPath("android4.txt"), "rw")
        val gson: Gson = Gson()
        println("写。。。。。。。。。。。。。")
        val strData = gson.toJson(message)
        println("strData.size : ${strData.length}")
        val array = strData.toByteArray(Charset.forName("ISO-8859-1"))
        println("strData.size : ${array.size}")
        AbsTransform.write(randomAccessFile, array)
        Thread.sleep(5 * 1000)
        println("读。。。。。。。。。。。。。")
        randomAccessFile.seek(0)
        val dataNew = AbsTransform.read(randomAccessFile)
        println("size : ${dataNew.size}")
        val json = String(dataNew)
        println("json = $json")
        val fromJson = gson.fromJson(json, MqttMessage::class.java)
        println("fromJson = $fromJson")
    }

    @Test
    fun t09() {
        val randomAccessFile = RandomAccessFile(createPath("android5.txt"), "rw")
        write(randomAccessFile, 99999)
        Thread.sleep(1 * 1000)
        write(randomAccessFile, 44444)
        Thread.sleep(1 * 1000)
        read(randomAccessFile)
        Thread.sleep(1 * 1000)
        read(randomAccessFile)
        Thread.sleep(1 * 1000)
    }

    private fun read(randomAccessFile: RandomAccessFile) {
        println("读。。。。。。。。。。。。。")
        val gson: Gson = Gson()
        randomAccessFile.seek(0)
        val dataNew = AbsTransform.read(randomAccessFile)
        println("size : ${dataNew.size}")
        val json = String(dataNew)
        println("json = $json")
        val fromJson = gson.fromJson(json, MqttMessage::class.java)
        println("fromJson = $fromJson")
    }

    private fun write(randomAccessFile: RandomAccessFile, index: Int) {
        val message = MqttMessage(
            System.currentTimeMillis(),
            (index % 2).toInt(),
            "t->$index $TEXT",
            index.toLong(),
            "DESC $TEXT",
            "TITLE $TEXT",
            "NAME $TEXT"
        )
        // T:MessageLite -> byte[] -> String -> byte[] -> T:MessageLite

        val gson: Gson = Gson()
        println("写。。。。。。。。。。。。。")
        val strData = gson.toJson(message)
        println("strData.size : ${strData.length}")
//        val array = strData.toByteArray(Charset.forName("ISO-8859-1"))
        val array = strData.toByteArray()
        println("strData.size : ${array.size}")
        AbsTransform.write(randomAccessFile, array)
    }

    @Test
    fun t10() {
        val gson = Gson()
        val index = 1
        val message = MqttMessage(
            System.currentTimeMillis(),
            (index % 2).toInt(),
            "t->$index 危楼高百尺",
            index.toLong(),
            "DESC 手可摘星辰",
            "TITLE 不敢高声语",
            "NAME 恐惊天上人"
        )
        println(message)
        val json: String = gson.toJson(message)
        val data = json.toByteArray()
        val out = ByteArrayOutputStream()
        out.write(data)
        val newJson = String(out.toByteArray())
        val newMessage = gson.fromJson<MqttMessage>(newJson, object : TypeToken<MqttMessage>() {}.type)
        println(newMessage)



    }


}