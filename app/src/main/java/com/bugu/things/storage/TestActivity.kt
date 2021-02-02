package com.bugu.things.storage

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bugu.queue.android.AndroidFileQueue
import com.bugu.queue.android.Ext
import com.bugu.queue.bean._MqttMessage
import com.bugu.things.storage.bean.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class TestActivity : AppCompatActivity() {
    private var fileQueue: AndroidFileQueue<MqttMessage>? = null
    private var fileQueueProto: AndroidFileQueue<_MqttMessage.MqttMessage>? = null

    private val toWrite: String = TEXT_01

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        request()
        initGson()
        initProto()
    }

    private fun initGson() {
        tv_message_gson.movementMethod = ScrollingMovementMethod.getInstance()
        btn_put_gson.setOnClickListener {
            if (fileQueue == null || fileQueue?.isClosed == true) {
                fileQueue = createFileQueue()
            }
            lifecycleScope.launch {
                launch(Dispatchers.IO) {
                    try {
                        var index = 0L
                        while (true) {
                            fileQueue?.put(
                                MqttMessage(
                                    System.currentTimeMillis(),
                                    (index % 2).toInt(),
                                    "t->$index $toWrite",
                                    index.toLong(),
                                    "DESC ",
                                    "TITLE ",
                                    "NAME "
                                )
                            )
                            index++
                            if (index > 10000000L) {
                                index = 0
                            }
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }
        }

        btn_take_gson.setOnClickListener {
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
                tv_message_gson.text = message.toString()

            }
        }

        btn_delete_gson.setOnClickListener {

            lifecycleScope.launch {
                val r = suspendDialog {
                    setMessage("是否删除GSON？")
                }
                if (r){
                    val delete = fileQueue?.delete()
                    tv_info_gson.text = "delete -> $delete !"
                }
            }

        }
    }

    private fun initProto() {
        tv_message_proto.movementMethod = ScrollingMovementMethod.getInstance()
        btn_put_proto.setOnClickListener {
            if (fileQueueProto == null || fileQueueProto?.isClosed == true) {
                fileQueueProto = createFileQueueProto()
            }
            lifecycleScope.launch {
                launch(Dispatchers.IO) {
                    try {
                        var index = 0L
                        while (true) {
                            fileQueueProto?.put(createMqttMessage(index))
                            index++
                            if (index > 10000000L) {
                                index = 0
                            }
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }
        }

        btn_take_proto.setOnClickListener {
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
                tv_message_proto.text = message?.print() ?: "-null"
            }
        }

        btn_delete_proto.setOnClickListener {
            lifecycleScope.launch {
                val r = suspendDialog {
                    setMessage("是否删除Proto？")
                }
                if (r){
                    val delete = fileQueueProto?.delete()
                    tv_info_proto.text = "delete -> $delete !"
                }
            }
        }
    }

    private fun createFileQueueProto(): AndroidFileQueue<_MqttMessage.MqttMessage> =
        Ext.createProtobufFileQueue(
            this,
            path("proto/test01"),
            _MqttMessage.MqttMessage::class.java
        ).apply {
            setOnFileChanged { _, logger, full ->
                updateProtoInfo(logger)
                lifecycleScope.launch {
                    if (full) {
                        ll_proto.setBackgroundColor(Color.parseColor("#ff9988"))
                        tv_message_proto.text = "满"
                    }
                }
            }
        }

    private fun createFileQueue(): AndroidFileQueue<MqttMessage> {
        val path = path("gson/test01")
        return Ext.createGsonFileQueue(this, path, MqttMessage::class.java).apply {
            setOnFileChanged { _, logger, full ->
                updateGsonInfo(logger)
                lifecycleScope.launch {
                    if (full) {
                        ll_gson.setBackgroundColor(Color.parseColor("#ff9988"))
                        tv_message_gson.text = "满"
                    }
                }
            }
        }
    }

    private fun createMqttMessage(index: Long): _MqttMessage.MqttMessage =
        _MqttMessage.MqttMessage.newBuilder()
            .setTime(System.currentTimeMillis())
            .setContent("$index -> $toWrite 我哎学习\n\n\n\n\n\n\n我哎学习")
            .setId(index)
            .setType((index % 2).toInt())
            .build()


    private fun updateGsonInfo(msg: String) {
        lifecycleScope.launch {
            tv_info_gson.text = msg
        }
    }

    private fun updateProtoInfo(msg: String) {
        lifecycleScope.launch {
            tv_info_proto.text = msg
        }
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

    private fun path(fileName: String): String {
        val path = getPath(applicationContext, fileName)
        Log.i("FileQueue", "path  = $path")
        return path
    }

    override fun onDestroy() {
        super.onDestroy()
        fileQueue?.close()
        fileQueueProto?.close()
    }
}