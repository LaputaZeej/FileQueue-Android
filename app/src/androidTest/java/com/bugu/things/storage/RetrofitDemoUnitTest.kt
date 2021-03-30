package com.bugu.things.storage

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bugu.things.storage.bean.A
import com.bugu.things.storage.bean.B
import com.bugu.things.storage.bean.MQMessage
import com.bugu.things.storage.core.StorageProxy
import com.bugu.things.storage.retrofit.HttpFunctions
import com.bugu.things.storage.retrofit.ObserverCallBack
import com.bugu.things.storage.retrofit.print
import com.bugu.things.storage.type.MyStorage
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Author by xpl, Date on 2021/2/8.
 */

@RunWith(AndroidJUnit4::class)
class RetrofitDemoUnitTest {
    @Test
    fun t1() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        "********************************************************************************************** a".print()
        val json = HttpFunctions.instance.getJson(object : ObserverCallBack {
            override fun handleResult(data: String?, encoding: Int, method: Int) {
                data.print()
            }

        }, "1")
        "result = $json".print()
        "********************************************************************************************** z".print()
        Thread.sleep(3000)
    }

    @Test
    fun t2(){
        "********************************************************************************************** a".print()
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val create = StorageProxy().apply { path = com.bugu.things.storage.ext.getPath(appContext,"t2.txt") }.create(MyStorage::class.java)

//        val delete1 = create.delete()
//        delete1.print()

        val a = A<List<B>>(listOf(B("b1"), B("B2"), B("B3")))
        val put = create.put(a)
        put.print()
        val take = create.take()
        take.print()
        val delete = create.delete()
        delete.print()
        "********************************************************************************************** z".print()
        Thread.sleep(5000)
    }
}