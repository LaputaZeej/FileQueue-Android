package com.bugu.things.storage

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bugu.queue.android.AndroidFileQueue
import com.bugu.queue.android.Ext
import com.bugu.queue.bean._MqttMessage
import com.bugu.things.storage.bean.MqttMessage
import com.bugu.things.storage.bean.print
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.lang.Runnable

class TestActivity : AppCompatActivity() {
    private var fileQueue: AndroidFileQueue<MqttMessage>? = null
    private var fileQueueProto: AndroidFileQueue<_MqttMessage.MqttMessage>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        request()
        tv_message.movementMethod = ScrollingMovementMethod.getInstance()

        btn_01.setOnClickListener {
            if (fileQueue == null || fileQueue?.isClosed == true) {
                fileQueue = createFileQueue()
            }
            lifecycleScope.launch {
                launch(Dispatchers.IO) {
                    try {
                        repeat(1000) { index ->
                            fileQueue?.put(
                                MqttMessage(
                                    System.currentTimeMillis(),
                                    (index % 2).toInt(),
                                    "t->$index $TEXT_ENGLISH",
                                    index.toLong(),
                                    "DESC ",
                                    "TITLE ",
                                    "NAME "
                                )
                            )
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }
        }

        btn_02.setOnClickListener {
            lifecycleScope.launch {
                if (fileQueue == null || fileQueue?.isClosed == true) {
                    fileQueue = createFileQueue()
                }
                val message = withContext(Dispatchers.IO) {
                    try {
                        fileQueue?.take()
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        null
                    }
                }
                tv_message.text = message.toString()

            }
        }


        btn_delete.setOnClickListener {
            val delete = fileQueue?.delete()
            val delete2 = fileQueueProto?.delete()
            tv_info.text = "gson delete $delete ! proti delete $delete2 !"
        }

        btn_put.setOnClickListener {
            if (fileQueueProto == null || fileQueueProto?.isClosed == true) {
                fileQueueProto = createFileQueueProto()
            }
            lifecycleScope.launch {
                launch(Dispatchers.IO) {
                    try {
                        repeat(1000) {
                            fileQueueProto?.put(createMqttMessage(it.toLong()))
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }
        }
        btn_take.setOnClickListener {
            if (fileQueueProto == null || fileQueueProto?.isClosed == true) {
                fileQueueProto = createFileQueueProto()
            }
            lifecycleScope.launch {
                val message = async(Dispatchers.IO) {
                    try {
                        val take = fileQueueProto?.take()
                        take
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        null
                    }
                }.await()
                tv_message.text = message?.print() ?: "-null"
                Log.i("xxxxx", "${message?.print() ?: "-null"}")
            }
        }

    }


    private fun createFileQueueProto(): AndroidFileQueue<_MqttMessage.MqttMessage> =
        Ext.createProtobufFileQueue(this, path("proto_2"), _MqttMessage.MqttMessage::class.java)
            .apply {
                setOnFileChanged { _, logger -> updateInfo(logger) }
            }

    private fun createMqttMessage(index: Long): _MqttMessage.MqttMessage =
        _MqttMessage.MqttMessage.newBuilder()
            .setTime(System.currentTimeMillis())
            .setContent("$index -> $TEXT_ENGLISH 我哎学习\n\n\n\n\n\n\n我哎学习")
            .setId(index)
            .setType((index % 2).toInt())
            .build()

    private fun createFileQueue(): AndroidFileQueue<MqttMessage> {
        val path = path()
        return Ext.createGsonFileQueue(this, path, MqttMessage::class.java).apply {
            setOnFileChanged { _, logger -> updateInfo(logger) }
        }
    }

    private fun updateInfo(msg: String) {
        lifecycleScope.launch {
            tv_info.text = msg
        }
    }

    private fun startThread(r: Runnable) {
        Thread(r).start()
    }

    private fun request() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0x11
            )
        }
    }

    private fun path(tag: String = ""): String {
        val file =
            android.os.Environment.getExternalStoragePublicDirectory("apk/${tag}takeAndPutMutable_0000009_10G.txt")
//            android.os.Environment.getExternalStoragePublicDirectory("apk/takeAndPutMutable_0000009_13G.txt")
        Log.i("FileQueue", "file ${file.exists()} path = ${file.absolutePath} ")
        return file.absolutePath
    }

    override fun onDestroy() {
        super.onDestroy()
        fileQueue?.close()
    }
}