package com.bugu.things.storage

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bugu.queue.FileQueue
import com.bugu.queue.MutableFileQueue
import com.bugu.queue.bean.GenericityEntity
import com.bugu.queue.bean._MqttMessage
import com.bugu.things.storage.bean.*
import com.bugu.things.storage.ext.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import com.bugu.things.storage.bean.MqttMessage

class TestActivity : AppCompatActivity() {
    private var fileQueue: MutableFileQueue<List<MqttMessage>>? = null
    private var fileQueueProto: MutableFileQueue<_MqttMessage.MqttMessage>? = null

    private val toWrite: String = TEXT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //request()
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
                                listOf(
                                    MqttMessage(
                                        System.currentTimeMillis(),
                                        (index % 2).toInt(),
                                        "t->$index $toWrite",
                                        index.toLong()
                                    ), MqttMessage(
                                        System.currentTimeMillis(),
                                        (index % 2).toInt(),
                                        "t->$index $toWrite",
                                        index.toLong()
                                    ), MqttMessage(
                                        System.currentTimeMillis(),
                                        (index % 2).toInt(),
                                        "t->$index $toWrite",
                                        index.toLong()
                                    ), MqttMessage(
                                        System.currentTimeMillis(),
                                        (index % 2).toInt(),
                                        "t->$index $toWrite",
                                        index.toLong()
                                    ), MqttMessage(
                                        System.currentTimeMillis(),
                                        (index % 2).toInt(),
                                        "t->$index $toWrite",
                                        index.toLong()

                                    ), MqttMessage(
                                        System.currentTimeMillis(),
                                        (index % 2).toInt(),
                                        "t->$index $toWrite",
                                        index.toLong()

                                    )
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
                        val take = fileQueue?.take()
                        take
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
                if (r) {
                    val delete = fileQueue?.delete()
                    tv_info_gson.text = "delete -> $delete !"
                    ll_gson.setBackgroundColor(Color.parseColor("#ffffff"))
                }
            }

        }
    }

    var takeProtoJob: Job? = null
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
            takeProtoJob?.cancel()
            if (fileQueueProto == null || fileQueueProto?.isClosed == true) {
                fileQueueProto = createFileQueueProto()
            }
            takeProtoJob = lifecycleScope.launch {
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
                if (r) {
                    val delete = fileQueueProto?.delete()
                    tv_info_proto.text = "delete -> $delete !"
                    ll_proto.setBackgroundColor(Color.parseColor("#000000"))
                }
            }
        }
    }

    private fun createFileQueueProto(): MutableFileQueue<_MqttMessage.MqttMessage> =
        createProtoFileQueue<_MqttMessage.MqttMessage>(path("proto/test01"))
            .apply {

                setOnFileQueueChanged { _, _, header ->
                    updateProtoInfo(header.logger(this@TestActivity, this.max))
                }
                setOnFileQueueStateChanged { _, state ->
                    if (state == FileQueue.State.FULL) {
                        ll_proto.setBackgroundColor(Color.parseColor("#ff9988"))
                    }
                }
            }

    private fun createFileQueue(): MutableFileQueue<List<MqttMessage>> {
        val path = path("gson/test01")
        return createGsonFileQueue<List<MqttMessage>>(path)
            .apply {
                setOnFileQueueChanged { _, _, header ->
                    updateGsonInfo(header.logger(this@TestActivity, this.max))
                }
                setOnFileQueueStateChanged { _, state ->
                    if (state == FileQueue.State.FULL) {
                        ll_gson.setBackgroundColor(Color.parseColor("#ff9988"))
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

    fun test() {
        val message = _MqttMessage.MqttMessage.newBuilder()
            .setContent("abc123 $TEXT 456xyz")
            .setId(1)
            .setTime(System.currentTimeMillis())
            .setType(0)
            .build()
        val pack = com.google.protobuf.Any.pack<_MqttMessage.MqttMessage>(message)
        GenericityEntity.FileQueueMessage.newBuilder().setData(pack).build()
    }
}