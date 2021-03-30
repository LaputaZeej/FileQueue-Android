package com.bugu.things.storage.retrofit


import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bugu.things.storage.R
import com.bugu.things.storage.bean.A
import com.bugu.things.storage.bean.B
import com.bugu.things.storage.core.StorageProxy
import com.bugu.things.storage.type.MyStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import java.net.URL
import kotlin.concurrent.thread

class RetrofitActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        thread {
            // https://www.wanandroid.com/article/list/0/json?cid=1
            URL(HttpConfig.ROOT_URL + "article/list/0/json?cid=1").readText().print()
        }//这里,如果没问题的话就会在logcat中打印出本次网络请求返回的数


        val create = StorageProxy().apply {
            path = com.bugu.things.storage.ext.getPath(applicationContext, "t1.txt")
        }.create(
            MyStorage::class.java
        )
        lifecycleScope.launch {
            lifecycleScope.launch(Dispatchers.IO) {
                delay(2000)
                repeat(100) {
                    val a = A<List<B>>(listOf(B("$it->b1"), B("$it->B2"), B("$it->B3")))
                    val put = create.put(a)
                    put.print()
                }
            }
            lifecycleScope.launch(Dispatchers.IO) {
                while (true) {
                    delay(2000)
                    val take = create.take()
                    launch(Dispatchers.Main) {
                        tv_message_proto.text = take.toString()
                    }
                    take.print()
                }
            }
            val delete = create.delete()
            delete.print()
        }

    }

}