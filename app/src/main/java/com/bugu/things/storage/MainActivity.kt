package com.bugu.things.storage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.format.Formatter
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    /*private var fileQueue: MutableFileQueue<MqttMessage>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        request()
        tv_message.movementMethod = ScrollingMovementMethod.getInstance()

        btn_01.setOnClickListener {
            if (fileQueue == null) {
                fileQueue = createFileQueue()
            }
            startThread(Write(fileQueue!!))
        }

        btn_02.setOnClickListener {
            lifecycleScope.launch {
                if (fileQueue == null) {
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

        tv_message.setOnClickListener {
            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.CALL_PHONE
                    ),
                    0x11
                )
            } else {
                callRightNow("123456")
            }
        }

    }

    private fun callRightNow(phone: String) {
        // 拨号：激活系统的拨号组件
        val intent = Intent() // 意图对象：动作 + 数据
        intent.action = Intent.ACTION_CALL // 设置动作
        val data = Uri.parse("tel:$phone") // 设置数据
        intent.data = data
        startActivity(intent) // 激活Activity组件
    }

    private fun createFileQueue(): MutableFileQueue<MqttMessage> {
        val path = path()
        val maxSize = Size._G * 10
        val MAX = Formatter.formatFileSize(applicationContext, maxSize)
        return MutableFileQueue(
            path,
            maxSize,
            GsonTransform<MqttMessage>(MqttMessage::class.java)
        ).apply {
            setOnFileQueueChanged { _, type, fileQueueHeader ->
                updateInfo(
                    "[ head:${fileQueueHeader.head} tail:${fileQueueHeader.tail} ] " +
                            Formatter.formatFileSize(applicationContext, fileQueueHeader.length) +
                            "/$MAX ${String.format("%d%%", fileQueueHeader.length * 100 / maxSize)}"
                )
            }
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

    private class Write(val fileQueue: MutableFileQueue<MqttMessage>) : Runnable {
        override fun run() {
            var index = 0L
            while (true) {
                try {
                    val message = MqttMessage(
                        System.currentTimeMillis(),
                        (index % 2).toInt(),
                        "t->$index $TEXT",
                        index
                    )
                    fileQueue.put(message)
                    index++
                    Thread.sleep(100)
                } catch (e: Throwable) {
                    e.printStackTrace()
                    break
                }
            }
        }
    }

    private class Read(val fileQueue: MutableFileQueue<MqttMessage>) : Runnable {
        override fun run() {
            while (true) {
                try {
                    val take = fileQueue.take()
                    Log.i("FileQueue", take.toString())
                    Thread.sleep(2000)
                } catch (e: Throwable) {
                    e.printStackTrace()
                    break
                }
            }
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

    private fun path(): String {
        val file =
            android.os.Environment.getExternalStoragePublicDirectory("apk/takeAndPutMutable_0000009_10G.txt")
//            android.os.Environment.getExternalStoragePublicDirectory("apk/takeAndPutMutable_0000009_13G.txt")

        Log.i("FileQueue", "file ${file.exists()} path = ${file.absolutePath} ")
        return file.absolutePath
    }

    override fun onDestroy() {
        super.onDestroy()
        fileQueue?.close()
    }

    companion object {
        const val TEXT =
            "五线谱最早的发源地是希腊，它的历史要比数字形的简谱早得多。在古希腊，音乐的主要表现形式是声乐，歌词发音的高低长短是用A、B、C……等字母表示的，到了罗马时代，开始用另一种符号来表示音的高低，这种记谱法称为“纽姆记谱法”（Neuma），这就是五线谱的雏形。“Neuma”源自希腊语，意为符号，是用绘图的形式表示的初期纽姆形状（图1-1）。\n" +
                    "这些纽姆符号开关清晰，有时表明一个音，也常常表明一组音，它能够帮助演唱者记忆、了解各种曲调进行特征，但它不能表示音的长短，也没有固定的高低位置，于是后人便划出一根直线，将纽姆符号写在线的上下，以线为中心点，把音固定为F，再根据上下位置确定音高，这种形式称为“一线谱”。\n" +
                    "到了11世纪，僧人规多把纽姆符号放在四根线上，从而确定其音高，这种乐谱称为“四线乐谱”。开始的线谱是用不同的颜色画成的，如红线代表F音，黄线或绿线代表C音（图1-2）。\n" +
                    "到了13世纪，四线乐谱采用全部黑色线，只是在线的前端写上一个拉丁字母，以表示线的绝对音高。这就是我们今天所使用的雏形。\n" +
                    "由于四线的纽姆乐谱并不能把节奏标出来，因此必须对每个音的长短有精确的确定方法，这就是定量音乐的起因。13世纪，科伦对约翰教学乐僧佛兰克著《定理歌曲艺术》一书首创了黑音符的长度（图1-3）。\n" +
                    "15世纪时，出现了白音符，音符种类也增加了。线谱发展到这种状态时，已基本能记录音的高低位置和音长短。到了16世纪，开始使用划分小节的记谱法，符头也变成了圆形。17世纪，四线谱又被改进为五线谱，经过300年的逐步完善，现已成为当今世界上公用的音乐记谱法。\n" +
                    "五线谱传入中国，最早见于文字记载的是1713年的《律吕正义》续编，书中记述了五线谱及音阶、唱名等。五线谱在中国逐步流传和使用，则于19世纪中叶以后随西方传教士的传教及新学的兴办而有所推广" +
                    "五线谱最早的发源地是希腊，它的历史要比数字形的简谱早得多。在古希腊，音乐的主要表现形式是声乐，歌词发音的高低长短是用A、B、C……等字母表示的，到了罗马时代，开始用另一种符号来表示音的高低，这种记谱法称为“纽姆记谱法”（Neuma），这就是五线谱的雏形。“Neuma”源自希腊语，意为符号，是用绘图的形式表示的初期纽姆形状（图1-1）。\n" +
                    "这些纽姆符号开关清晰，有时表明一个音，也常常表明一组音，它能够帮助演唱者记忆、了解各种曲调进行特征，但它不能表示音的长短，也没有固定的高低位置，于是后人便划出一根直线，将纽姆符号写在线的上下，以线为中心点，把音固定为F，再根据上下位置确定音高，这种形式称为“一线谱”。\n" +
                    "到了11世纪，僧人规多把纽姆符号放在四根线上，从而确定其音高，这种乐谱称为“四线乐谱”。开始的线谱是用不同的颜色画成的，如红线代表F音，黄线或绿线代表C音（图1-2）。\n" +
                    "到了13世纪，四线乐谱采用全部黑色线，只是在线的前端写上一个拉丁字母，以表示线的绝对音高。这就是我们今天所使用的雏形。\n" +
                    "由于四线的纽姆乐谱并不能把节奏标出来，因此必须对每个音的长短有精确的确定方法，这就是定量音乐的起因。13世纪，科伦对约翰教学乐僧佛兰克著《定理歌曲艺术》一书首创了黑音符的长度（图1-3）。\n" +
                    "15世纪时，出现了白音符，音符种类也增加了。线谱发展到这种状态时，已基本能记录音的高低位置和音长短。到了16世纪，开始使用划分小节的记谱法，符头也变成了圆形。17世纪，四线谱又被改进为五线谱，经过300年的逐步完善，现已成为当今世界上公用的音乐记谱法。\n" +
                    "五线谱传入中国，最早见于文字记载的是1713年的《律吕正义》续编，书中记述了五线谱及音阶、唱名等。五线谱在中国逐步流传和使用，则于19世纪中叶以后随西方传教士的传教及新学的兴办而有所推广" +
                    "五线谱最早的发源地是希腊，它的历史要比数字形的简谱早得多。在古希腊，音乐的主要表现形式是声乐，歌词发音的高低长短是用A、B、C……等字母表示的，到了罗马时代，开始用另一种符号来表示音的高低，这种记谱法称为“纽姆记谱法”（Neuma），这就是五线谱的雏形。“Neuma”源自希腊语，意为符号，是用绘图的形式表示的初期纽姆形状（图1-1）。\n" +
                    "这些纽姆符号开关清晰，有时表明一个音，也常常表明一组音，它能够帮助演唱者记忆、了解各种曲调进行特征，但它不能表示音的长短，也没有固定的高低位置，于是后人便划出一根直线，将纽姆符号写在线的上下，以线为中心点，把音固定为F，再根据上下位置确定音高，这种形式称为“一线谱”。\n" +
                    "到了11世纪，僧人规多把纽姆符号放在四根线上，从而确定其音高，这种乐谱称为“四线乐谱”。开始的线谱是用不同的颜色画成的，如红线代表F音，黄线或绿线代表C音（图1-2）。\n" +
                    "到了13世纪，四线乐谱采用全部黑色线，只是在线的前端写上一个拉丁字母，以表示线的绝对音高。这就是我们今天所使用的雏形。\n" +
                    "由于四线的纽姆乐谱并不能把节奏标出来，因此必须对每个音的长短有精确的确定方法，这就是定量音乐的起因。13世纪，科伦对约翰教学乐僧佛兰克著《定理歌曲艺术》一书首创了黑音符的长度（图1-3）。\n" +
                    "15世纪时，出现了白音符，音符种类也增加了。线谱发展到这种状态时，已基本能记录音的高低位置和音长短。到了16世纪，开始使用划分小节的记谱法，符头也变成了圆形。17世纪，四线谱又被改进为五线谱，经过300年的逐步完善，现已成为当今世界上公用的音乐记谱法。\n" +
                    "五线谱传入中国，最早见于文字记载的是1713年的《律吕正义》续编，书中记述了五线谱及音阶、唱名等。五线谱在中国逐步流传和使用，则于19世纪中叶以后随西方传教士的传教及新学的兴办而有所推广" +
                    "五线谱最早的发源地是希腊，它的历史要比数字形的简谱早得多。在古希腊，音乐的主要表现形式是声乐，歌词发音的高低长短是用A、B、C……等字母表示的，到了罗马时代，开始用另一种符号来表示音的高低，这种记谱法称为“纽姆记谱法”（Neuma），这就是五线谱的雏形。“Neuma”源自希腊语，意为符号，是用绘图的形式表示的初期纽姆形状（图1-1）。\n" +
                    "这些纽姆符号开关清晰，有时表明一个音，也常常表明一组音，它能够帮助演唱者记忆、了解各种曲调进行特征，但它不能表示音的长短，也没有固定的高低位置，于是后人便划出一根直线，将纽姆符号写在线的上下，以线为中心点，把音固定为F，再根据上下位置确定音高，这种形式称为“一线谱”。\n" +
                    "到了11世纪，僧人规多把纽姆符号放在四根线上，从而确定其音高，这种乐谱称为“四线乐谱”。开始的线谱是用不同的颜色画成的，如红线代表F音，黄线或绿线代表C音（图1-2）。\n" +
                    "到了13世纪，四线乐谱采用全部黑色线，只是在线的前端写上一个拉丁字母，以表示线的绝对音高。这就是我们今天所使用的雏形。\n" +
                    "由于四线的纽姆乐谱并不能把节奏标出来，因此必须对每个音的长短有精确的确定方法，这就是定量音乐的起因。13世纪，科伦对约翰教学乐僧佛兰克著《定理歌曲艺术》一书首创了黑音符的长度（图1-3）。\n" +
                    "15世纪时，出现了白音符，音符种类也增加了。线谱发展到这种状态时，已基本能记录音的高低位置和音长短。到了16世纪，开始使用划分小节的记谱法，符头也变成了圆形。17世纪，四线谱又被改进为五线谱，经过300年的逐步完善，现已成为当今世界上公用的音乐记谱法。\n" +
                    "五线谱传入中国，最早见于文字记载的是1713年的《律吕正义》续编，书中记述了五线谱及音阶、唱名等。五线谱在中国逐步流传和使用，则于19世纪中叶以后随西方传教士的传教及新学的兴办而有所推广" +
                    "五线谱最早的发源地是希腊，它的历史要比数字形的简谱早得多。在古希腊，音乐的主要表现形式是声乐，歌词发音的高低长短是用A、B、C……等字母表示的，到了罗马时代，开始用另一种符号来表示音的高低，这种记谱法称为“纽姆记谱法”（Neuma），这就是五线谱的雏形。“Neuma”源自希腊语，意为符号，是用绘图的形式表示的初期纽姆形状（图1-1）。\n" +
                    "这些纽姆符号开关清晰，有时表明一个音，也常常表明一组音，它能够帮助演唱者记忆、了解各种曲调进行特征，但它不能表示音的长短，也没有固定的高低位置，于是后人便划出一根直线，将纽姆符号写在线的上下，以线为中心点，把音固定为F，再根据上下位置确定音高，这种形式称为“一线谱”。\n" +
                    "到了11世纪，僧人规多把纽姆符号放在四根线上，从而确定其音高，这种乐谱称为“四线乐谱”。开始的线谱是用不同的颜色画成的，如红线代表F音，黄线或绿线代表C音（图1-2）。\n" +
                    "到了13世纪，四线乐谱采用全部黑色线，只是在线的前端写上一个拉丁字母，以表示线的绝对音高。这就是我们今天所使用的雏形。\n" +
                    "由于四线的纽姆乐谱并不能把节奏标出来，因此必须对每个音的长短有精确的确定方法，这就是定量音乐的起因。13世纪，科伦对约翰教学乐僧佛兰克著《定理歌曲艺术》一书首创了黑音符的长度（图1-3）。\n" +
                    "15世纪时，出现了白音符，音符种类也增加了。线谱发展到这种状态时，已基本能记录音的高低位置和音长短。到了16世纪，开始使用划分小节的记谱法，符头也变成了圆形。17世纪，四线谱又被改进为五线谱，经过300年的逐步完善，现已成为当今世界上公用的音乐记谱法。\n" +
                    "五线谱传入中国，最早见于文字记载的是1713年的《律吕正义》续编，书中记述了五线谱及音阶、唱名等。五线谱在中国逐步流传和使用，则于19世纪中叶以后随西方传教士的传教及新学的兴办而有所推广" +
                    "五线谱最早的发源地是希腊，它的历史要比数字形的简谱早得多。在古希腊，音乐的主要表现形式是声乐，歌词发音的高低长短是用A、B、C……等字母表示的，到了罗马时代，开始用另一种符号来表示音的高低，这种记谱法称为“纽姆记谱法”（Neuma），这就是五线谱的雏形。“Neuma”源自希腊语，意为符号，是用绘图的形式表示的初期纽姆形状（图1-1）。\n" +
                    "这些纽姆符号开关清晰，有时表明一个音，也常常表明一组音，它能够帮助演唱者记忆、了解各种曲调进行特征，但它不能表示音的长短，也没有固定的高低位置，于是后人便划出一根直线，将纽姆符号写在线的上下，以线为中心点，把音固定为F，再根据上下位置确定音高，这种形式称为“一线谱”。\n" +
                    "到了11世纪，僧人规多把纽姆符号放在四根线上，从而确定其音高，这种乐谱称为“四线乐谱”。开始的线谱是用不同的颜色画成的，如红线代表F音，黄线或绿线代表C音（图1-2）。\n" +
                    "到了13世纪，四线乐谱采用全部黑色线，只是在线的前端写上一个拉丁字母，以表示线的绝对音高。这就是我们今天所使用的雏形。\n" +
                    "由于四线的纽姆乐谱并不能把节奏标出来，因此必须对每个音的长短有精确的确定方法，这就是定量音乐的起因。13世纪，科伦对约翰教学乐僧佛兰克著《定理歌曲艺术》一书首创了黑音符的长度（图1-3）。\n" +
                    "15世纪时，出现了白音符，音符种类也增加了。线谱发展到这种状态时，已基本能记录音的高低位置和音长短。到了16世纪，开始使用划分小节的记谱法，符头也变成了圆形。17世纪，四线谱又被改进为五线谱，经过300年的逐步完善，现已成为当今世界上公用的音乐记谱法。\n" +
                    "五线谱传入中国，最早见于文字记载的是1713年的《律吕正义》续编，书中记述了五线谱及音阶、唱名等。五线谱在中国逐步流传和使用，则于19世纪中叶以后随西方传教士的传教及新学的兴办而有所推广" +
                    "五线谱最早的发源地是希腊，它的历史要比数字形的简谱早得多。在古希腊，音乐的主要表现形式是声乐，歌词发音的高低长短是用A、B、C……等字母表示的，到了罗马时代，开始用另一种符号来表示音的高低，这种记谱法称为“纽姆记谱法”（Neuma），这就是五线谱的雏形。“Neuma”源自希腊语，意为符号，是用绘图的形式表示的初期纽姆形状（图1-1）。\n" +
                    "这些纽姆符号开关清晰，有时表明一个音，也常常表明一组音，它能够帮助演唱者记忆、了解各种曲调进行特征，但它不能表示音的长短，也没有固定的高低位置，于是后人便划出一根直线，将纽姆符号写在线的上下，以线为中心点，把音固定为F，再根据上下位置确定音高，这种形式称为“一线谱”。\n" +
                    "到了11世纪，僧人规多把纽姆符号放在四根线上，从而确定其音高，这种乐谱称为“四线乐谱”。开始的线谱是用不同的颜色画成的，如红线代表F音，黄线或绿线代表C音（图1-2）。\n" +
                    "到了13世纪，四线乐谱采用全部黑色线，只是在线的前端写上一个拉丁字母，以表示线的绝对音高。这就是我们今天所使用的雏形。\n" +
                    "由于四线的纽姆乐谱并不能把节奏标出来，因此必须对每个音的长短有精确的确定方法，这就是定量音乐的起因。13世纪，科伦对约翰教学乐僧佛兰克著《定理歌曲艺术》一书首创了黑音符的长度（图1-3）。\n" +
                    "15世纪时，出现了白音符，音符种类也增加了。线谱发展到这种状态时，已基本能记录音的高低位置和音长短。到了16世纪，开始使用划分小节的记谱法，符头也变成了圆形。17世纪，四线谱又被改进为五线谱，经过300年的逐步完善，现已成为当今世界上公用的音乐记谱法。\n" +
                    "五线谱传入中国，最早见于文字记载的是1713年的《律吕正义》续编，书中记述了五线谱及音阶、唱名等。五线谱在中国逐步流传和使用，则于19世纪中叶以后随西方传教士的传教及新学的兴办而有所推广" +
                    "五线谱最早的发源地是希腊，它的历史要比数字形的简谱早得多。在古希腊，音乐的主要表现形式是声乐，歌词发音的高低长短是用A、B、C……等字母表示的，到了罗马时代，开始用另一种符号来表示音的高低，这种记谱法称为“纽姆记谱法”（Neuma），这就是五线谱的雏形。“Neuma”源自希腊语，意为符号，是用绘图的形式表示的初期纽姆形状（图1-1）。\n" +
                    "这些纽姆符号开关清晰，有时表明一个音，也常常表明一组音，它能够帮助演唱者记忆、了解各种曲调进行特征，但它不能表示音的长短，也没有固定的高低位置，于是后人便划出一根直线，将纽姆符号写在线的上下，以线为中心点，把音固定为F，再根据上下位置确定音高，这种形式称为“一线谱”。\n" +
                    "到了11世纪，僧人规多把纽姆符号放在四根线上，从而确定其音高，这种乐谱称为“四线乐谱”。开始的线谱是用不同的颜色画成的，如红线代表F音，黄线或绿线代表C音（图1-2）。\n" +
                    "到了13世纪，四线乐谱采用全部黑色线，只是在线的前端写上一个拉丁字母，以表示线的绝对音高。这就是我们今天所使用的雏形。\n" +
                    "由于四线的纽姆乐谱并不能把节奏标出来，因此必须对每个音的长短有精确的确定方法，这就是定量音乐的起因。13世纪，科伦对约翰教学乐僧佛兰克著《定理歌曲艺术》一书首创了黑音符的长度（图1-3）。\n" +
                    "15世纪时，出现了白音符，音符种类也增加了。线谱发展到这种状态时，已基本能记录音的高低位置和音长短。到了16世纪，开始使用划分小节的记谱法，符头也变成了圆形。17世纪，四线谱又被改进为五线谱，经过300年的逐步完善，现已成为当今世界上公用的音乐记谱法。\n" +
                    "五线谱传入中国，最早见于文字记载的是1713年的《律吕正义》续编，书中记述了五线谱及音阶、唱名等。五线谱在中国逐步流传和使用，则于19世纪中叶以后随西方传教士的传教及新学的兴办而有所推广" +
                    "五线谱最早的发源地是希腊，它的历史要比数字形的简谱早得多。在古希腊，音乐的主要表现形式是声乐，歌词发音的高低长短是用A、B、C……等字母表示的，到了罗马时代，开始用另一种符号来表示音的高低，这种记谱法称为“纽姆记谱法”（Neuma），这就是五线谱的雏形。“Neuma”源自希腊语，意为符号，是用绘图的形式表示的初期纽姆形状（图1-1）。\n" +
                    "这些纽姆符号开关清晰，有时表明一个音，也常常表明一组音，它能够帮助演唱者记忆、了解各种曲调进行特征，但它不能表示音的长短，也没有固定的高低位置，于是后人便划出一根直线，将纽姆符号写在线的上下，以线为中心点，把音固定为F，再根据上下位置确定音高，这种形式称为“一线谱”。\n" +
                    "到了11世纪，僧人规多把纽姆符号放在四根线上，从而确定其音高，这种乐谱称为“四线乐谱”。开始的线谱是用不同的颜色画成的，如红线代表F音，黄线或绿线代表C音（图1-2）。\n" +
                    "到了13世纪，四线乐谱采用全部黑色线，只是在线的前端写上一个拉丁字母，以表示线的绝对音高。这就是我们今天所使用的雏形。\n" +
                    "由于四线的纽姆乐谱并不能把节奏标出来，因此必须对每个音的长短有精确的确定方法，这就是定量音乐的起因。13世纪，科伦对约翰教学乐僧佛兰克著《定理歌曲艺术》一书首创了黑音符的长度（图1-3）。\n" +
                    "15世纪时，出现了白音符，音符种类也增加了。线谱发展到这种状态时，已基本能记录音的高低位置和音长短。到了16世纪，开始使用划分小节的记谱法，符头也变成了圆形。17世纪，四线谱又被改进为五线谱，经过300年的逐步完善，现已成为当今世界上公用的音乐记谱法。\n" +
                    "五线谱传入中国，最早见于文字记载的是1713年的《律吕正义》续编，书中记述了五线谱及音阶、唱名等。五线谱在中国逐步流传和使用，则于19世纪中叶以后随西方传教士的传教及新学的兴办而有所推广" +
                    "五线谱最早的发源地是希腊，它的历史要比数字形的简谱早得多。在古希腊，音乐的主要表现形式是声乐，歌词发音的高低长短是用A、B、C……等字母表示的，到了罗马时代，开始用另一种符号来表示音的高低，这种记谱法称为“纽姆记谱法”（Neuma），这就是五线谱的雏形。“Neuma”源自希腊语，意为符号，是用绘图的形式表示的初期纽姆形状（图1-1）。\n" +
                    "这些纽姆符号开关清晰，有时表明一个音，也常常表明一组音，它能够帮助演唱者记忆、了解各种曲调进行特征，但它不能表示音的长短，也没有固定的高低位置，于是后人便划出一根直线，将纽姆符号写在线的上下，以线为中心点，把音固定为F，再根据上下位置确定音高，这种形式称为“一线谱”。\n" +
                    "到了11世纪，僧人规多把纽姆符号放在四根线上，从而确定其音高，这种乐谱称为“四线乐谱”。开始的线谱是用不同的颜色画成的，如红线代表F音，黄线或绿线代表C音（图1-2）。\n" +
                    "到了13世纪，四线乐谱采用全部黑色线，只是在线的前端写上一个拉丁字母，以表示线的绝对音高。这就是我们今天所使用的雏形。\n" +
                    "由于四线的纽姆乐谱并不能把节奏标出来，因此必须对每个音的长短有精确的确定方法，这就是定量音乐的起因。13世纪，科伦对约翰教学乐僧佛兰克著《定理歌曲艺术》一书首创了黑音符的长度（图1-3）。\n" +
                    "15世纪时，出现了白音符，音符种类也增加了。线谱发展到这种状态时，已基本能记录音的高低位置和音长短。到了16世纪，开始使用划分小节的记谱法，符头也变成了圆形。17世纪，四线谱又被改进为五线谱，经过300年的逐步完善，现已成为当今世界上公用的音乐记谱法。\n" +
                    "五线谱传入中国，最早见于文字记载的是1713年的《律吕正义》续编，书中记述了五线谱及音阶、唱名等。五线谱在中国逐步流传和使用，则于19世纪中叶以后随西方传教士的传教及新学的兴办而有所推广" +
                    "五线谱最早的发源地是希腊，它的历史要比数字形的简谱早得多。在古希腊，音乐的主要表现形式是声乐，歌词发音的高低长短是用A、B、C……等字母表示的，到了罗马时代，开始用另一种符号来表示音的高低，这种记谱法称为“纽姆记谱法”（Neuma），这就是五线谱的雏形。“Neuma”源自希腊语，意为符号，是用绘图的形式表示的初期纽姆形状（图1-1）。\n" +
                    "这些纽姆符号开关清晰，有时表明一个音，也常常表明一组音，它能够帮助演唱者记忆、了解各种曲调进行特征，但它不能表示音的长短，也没有固定的高低位置，于是后人便划出一根直线，将纽姆符号写在线的上下，以线为中心点，把音固定为F，再根据上下位置确定音高，这种形式称为“一线谱”。\n" +
                    "到了11世纪，僧人规多把纽姆符号放在四根线上，从而确定其音高，这种乐谱称为“四线乐谱”。开始的线谱是用不同的颜色画成的，如红线代表F音，黄线或绿线代表C音（图1-2）。\n" +
                    "到了13世纪，四线乐谱采用全部黑色线，只是在线的前端写上一个拉丁字母，以表示线的绝对音高。这就是我们今天所使用的雏形。\n" +
                    "由于四线的纽姆乐谱并不能把节奏标出来，因此必须对每个音的长短有精确的确定方法，这就是定量音乐的起因。13世纪，科伦对约翰教学乐僧佛兰克著《定理歌曲艺术》一书首创了黑音符的长度（图1-3）。\n" +
                    "15世纪时，出现了白音符，音符种类也增加了。线谱发展到这种状态时，已基本能记录音的高低位置和音长短。到了16世纪，开始使用划分小节的记谱法，符头也变成了圆形。17世纪，四线谱又被改进为五线谱，经过300年的逐步完善，现已成为当今世界上公用的音乐记谱法。\n" +
                    "五线谱传入中国，最早见于文字记载的是1713年的《律吕正义》续编，书中记述了五线谱及音阶、唱名等。五线谱在中国逐步流传和使用，则于19世纪中叶以后随西方传教士的传教及新学的兴办而有所推广" +
                    "五线谱最早的发源地是希腊，它的历史要比数字形的简谱早得多。在古希腊，音乐的主要表现形式是声乐，歌词发音的高低长短是用A、B、C……等字母表示的，到了罗马时代，开始用另一种符号来表示音的高低，这种记谱法称为“纽姆记谱法”（Neuma），这就是五线谱的雏形。“Neuma”源自希腊语，意为符号，是用绘图的形式表示的初期纽姆形状（图1-1）。\n" +
                    "这些纽姆符号开关清晰，有时表明一个音，也常常表明一组音，它能够帮助演唱者记忆、了解各种曲调进行特征，但它不能表示音的长短，也没有固定的高低位置，于是后人便划出一根直线，将纽姆符号写在线的上下，以线为中心点，把音固定为F，再根据上下位置确定音高，这种形式称为“一线谱”。\n" +
                    "到了11世纪，僧人规多把纽姆符号放在四根线上，从而确定其音高，这种乐谱称为“四线乐谱”。开始的线谱是用不同的颜色画成的，如红线代表F音，黄线或绿线代表C音（图1-2）。\n" +
                    "到了13世纪，四线乐谱采用全部黑色线，只是在线的前端写上一个拉丁字母，以表示线的绝对音高。这就是我们今天所使用的雏形。\n" +
                    "由于四线的纽姆乐谱并不能把节奏标出来，因此必须对每个音的长短有精确的确定方法，这就是定量音乐的起因。13世纪，科伦对约翰教学乐僧佛兰克著《定理歌曲艺术》一书首创了黑音符的长度（图1-3）。\n" +
                    "15世纪时，出现了白音符，音符种类也增加了。线谱发展到这种状态时，已基本能记录音的高低位置和音长短。到了16世纪，开始使用划分小节的记谱法，符头也变成了圆形。17世纪，四线谱又被改进为五线谱，经过300年的逐步完善，现已成为当今世界上公用的音乐记谱法。\n" +
                    "五线谱传入中国，最早见于文字记载的是1713年的《律吕正义》续编，书中记述了五线谱及音阶、唱名等。五线谱在中国逐步流传和使用，则于19世纪中叶以后随西方传教士的传教及新学的兴办而有所推广" +
                    "五线谱最早的发源地是希腊，它的历史要比数字形的简谱早得多。在古希腊，音乐的主要表现形式是声乐，歌词发音的高低长短是用A、B、C……等字母表示的，到了罗马时代，开始用另一种符号来表示音的高低，这种记谱法称为“纽姆记谱法”（Neuma），这就是五线谱的雏形。“Neuma”源自希腊语，意为符号，是用绘图的形式表示的初期纽姆形状（图1-1）。\n" +
                    "这些纽姆符号开关清晰，有时表明一个音，也常常表明一组音，它能够帮助演唱者记忆、了解各种曲调进行特征，但它不能表示音的长短，也没有固定的高低位置，于是后人便划出一根直线，将纽姆符号写在线的上下，以线为中心点，把音固定为F，再根据上下位置确定音高，这种形式称为“一线谱”。\n" +
                    "到了11世纪，僧人规多把纽姆符号放在四根线上，从而确定其音高，这种乐谱称为“四线乐谱”。开始的线谱是用不同的颜色画成的，如红线代表F音，黄线或绿线代表C音（图1-2）。\n" +
                    "到了13世纪，四线乐谱采用全部黑色线，只是在线的前端写上一个拉丁字母，以表示线的绝对音高。这就是我们今天所使用的雏形。\n" +
                    "由于四线的纽姆乐谱并不能把节奏标出来，因此必须对每个音的长短有精确的确定方法，这就是定量音乐的起因。13世纪，科伦对约翰教学乐僧佛兰克著《定理歌曲艺术》一书首创了黑音符的长度（图1-3）。\n" +
                    "15世纪时，出现了白音符，音符种类也增加了。线谱发展到这种状态时，已基本能记录音的高低位置和音长短。到了16世纪，开始使用划分小节的记谱法，符头也变成了圆形。17世纪，四线谱又被改进为五线谱，经过300年的逐步完善，现已成为当今世界上公用的音乐记谱法。\n" +
                    "五线谱传入中国，最早见于文字记载的是1713年的《律吕正义》续编，书中记述了五线谱及音阶、唱名等。五线谱在中国逐步流传和使用，则于19世纪中叶以后随西方传教士的传教及新学的兴办而有所推广" +
                    "五线谱最早的发源地是希腊，它的历史要比数字形的简谱早得多。在古希腊，音乐的主要表现形式是声乐，歌词发音的高低长短是用A、B、C……等字母表示的，到了罗马时代，开始用另一种符号来表示音的高低，这种记谱法称为“纽姆记谱法”（Neuma），这就是五线谱的雏形。“Neuma”源自希腊语，意为符号，是用绘图的形式表示的初期纽姆形状（图1-1）。\n" +
                    "这些纽姆符号开关清晰，有时表明一个音，也常常表明一组音，它能够帮助演唱者记忆、了解各种曲调进行特征，但它不能表示音的长短，也没有固定的高低位置，于是后人便划出一根直线，将纽姆符号写在线的上下，以线为中心点，把音固定为F，再根据上下位置确定音高，这种形式称为“一线谱”。\n" +
                    "到了11世纪，僧人规多把纽姆符号放在四根线上，从而确定其音高，这种乐谱称为“四线乐谱”。开始的线谱是用不同的颜色画成的，如红线代表F音，黄线或绿线代表C音（图1-2）。\n" +
                    "到了13世纪，四线乐谱采用全部黑色线，只是在线的前端写上一个拉丁字母，以表示线的绝对音高。这就是我们今天所使用的雏形。\n" +
                    "由于四线的纽姆乐谱并不能把节奏标出来，因此必须对每个音的长短有精确的确定方法，这就是定量音乐的起因。13世纪，科伦对约翰教学乐僧佛兰克著《定理歌曲艺术》一书首创了黑音符的长度（图1-3）。\n" +
                    "15世纪时，出现了白音符，音符种类也增加了。线谱发展到这种状态时，已基本能记录音的高低位置和音长短。到了16世纪，开始使用划分小节的记谱法，符头也变成了圆形。17世纪，四线谱又被改进为五线谱，经过300年的逐步完善，现已成为当今世界上公用的音乐记谱法。\n" +
                    "五线谱传入中国，最早见于文字记载的是1713年的《律吕正义》续编，书中记述了五线谱及音阶、唱名等。五线谱在中国逐步流传和使用，则于19世纪中叶以后随西方传教士的传教及新学的兴办而有所推广" +
                    "五线谱最早的发源地是希腊，它的历史要比数字形的简谱早得多。在古希腊，音乐的主要表现形式是声乐，歌词发音的高低长短是用A、B、C……等字母表示的，到了罗马时代，开始用另一种符号来表示音的高低，这种记谱法称为“纽姆记谱法”（Neuma），这就是五线谱的雏形。“Neuma”源自希腊语，意为符号，是用绘图的形式表示的初期纽姆形状（图1-1）。\n" +
                    "这些纽姆符号开关清晰，有时表明一个音，也常常表明一组音，它能够帮助演唱者记忆、了解各种曲调进行特征，但它不能表示音的长短，也没有固定的高低位置，于是后人便划出一根直线，将纽姆符号写在线的上下，以线为中心点，把音固定为F，再根据上下位置确定音高，这种形式称为“一线谱”。\n" +
                    "到了11世纪，僧人规多把纽姆符号放在四根线上，从而确定其音高，这种乐谱称为“四线乐谱”。开始的线谱是用不同的颜色画成的，如红线代表F音，黄线或绿线代表C音（图1-2）。\n" +
                    "到了13世纪，四线乐谱采用全部黑色线，只是在线的前端写上一个拉丁字母，以表示线的绝对音高。这就是我们今天所使用的雏形。\n" +
                    "由于四线的纽姆乐谱并不能把节奏标出来，因此必须对每个音的长短有精确的确定方法，这就是定量音乐的起因。13世纪，科伦对约翰教学乐僧佛兰克著《定理歌曲艺术》一书首创了黑音符的长度（图1-3）。\n" +
                    "15世纪时，出现了白音符，音符种类也增加了。线谱发展到这种状态时，已基本能记录音的高低位置和音长短。到了16世纪，开始使用划分小节的记谱法，符头也变成了圆形。17世纪，四线谱又被改进为五线谱，经过300年的逐步完善，现已成为当今世界上公用的音乐记谱法。\n" +
                    "五线谱传入中国，最早见于文字记载的是1713年的《律吕正义》续编，书中记述了五线谱及音阶、唱名等。五线谱在中国逐步流传和使用，则于19世纪中叶以后随西方传教士的传教及新学的兴办而有所推广" +
                    "五线谱最早的发源地是希腊，它的历史要比数字形的简谱早得多。在古希腊，音乐的主要表现形式是声乐，歌词发音的高低长短是用A、B、C……等字母表示的，到了罗马时代，开始用另一种符号来表示音的高低，这种记谱法称为“纽姆记谱法”（Neuma），这就是五线谱的雏形。“Neuma”源自希腊语，意为符号，是用绘图的形式表示的初期纽姆形状（图1-1）。\n" +
                    "这些纽姆符号开关清晰，有时表明一个音，也常常表明一组音，它能够帮助演唱者记忆、了解各种曲调进行特征，但它不能表示音的长短，也没有固定的高低位置，于是后人便划出一根直线，将纽姆符号写在线的上下，以线为中心点，把音固定为F，再根据上下位置确定音高，这种形式称为“一线谱”。\n" +
                    "到了11世纪，僧人规多把纽姆符号放在四根线上，从而确定其音高，这种乐谱称为“四线乐谱”。开始的线谱是用不同的颜色画成的，如红线代表F音，黄线或绿线代表C音（图1-2）。\n" +
                    "到了13世纪，四线乐谱采用全部黑色线，只是在线的前端写上一个拉丁字母，以表示线的绝对音高。这就是我们今天所使用的雏形。\n" +
                    "由于四线的纽姆乐谱并不能把节奏标出来，因此必须对每个音的长短有精确的确定方法，这就是定量音乐的起因。13世纪，科伦对约翰教学乐僧佛兰克著《定理歌曲艺术》一书首创了黑音符的长度（图1-3）。\n" +
                    "15世纪时，出现了白音符，音符种类也增加了。线谱发展到这种状态时，已基本能记录音的高低位置和音长短。到了16世纪，开始使用划分小节的记谱法，符头也变成了圆形。17世纪，四线谱又被改进为五线谱，经过300年的逐步完善，现已成为当今世界上公用的音乐记谱法。\n" +
                    "五线谱传入中国，最早见于文字记载的是1713年的《律吕正义》续编，书中记述了五线谱及音阶、唱名等。五线谱在中国逐步流传和使用，则于19世纪中叶以后随西方传教士的传教及新学的兴办而有所推广"
    }*/
}