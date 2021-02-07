package com.bugu.things.storage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bugu.things.storage.bean.MQMessage
import com.bugu.things.storage.bean.Person
import com.bugu.things.storage.bean.Pet
import com.bugu.things.storage.core.FileQueueStorage
import com.bugu.things.storage.ext.TEXT
import com.bugu.things.storage.ext.getPath
import com.bugu.things.storage.test.DemoStorageImpl
import com.bugu.things.storage.test.StorageManager
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StorageActivity : AppCompatActivity() {

    private val toWrite: String = TEXT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)
        init2()
    }

    private fun init2() {
        DemoStorageImpl.getInstance().init(this,"storage-string-02.txt")
        lifecycleScope.launch {
            launch {
                repeat(100) {
                    DemoStorageImpl.getInstance().put("index -> $it")
                }
            }

            launch {
                while (true) {
                    delay(1000)
                    val take = DemoStorageImpl.getInstance().take()
                    println("take -> $take")
                }
            }
        }
    }

    private fun init() {
        val fileQueueStorage: FileQueueStorage<Person<Pet>> =
            FileQueueStorage(
                this.applicationContext,
                getPath(this, "storage-01.txt"),
                0L,
                0L,
                object : TypeToken<MQMessage<Person<Pet>>>() {}
            )
        StorageManager.getInstance().setStorage(fileQueueStorage)
        lifecycleScope.launch {
            launch {
                repeat(100) {
                    StorageManager.getInstance().put(createData3(it))
                }
            }

            launch {
                while (true) {
                    delay(1000)
                    val take = StorageManager.getInstance()
                        .take()
                    println("take->$take")
                }
            }
        }
    }

    private fun createData(index: Int) = listOf<String>("$index 猫", "$index 狗", "$index 猪")
    private fun createData2(index: Int) = listOf<Person<Pet>>(
        Person("小明$index", Pet("小狗${index}号")),
        Person("小天$index", Pet("小猪${index}号")),
        Person("小你$index", Pet("小猫${index}号"))
    )

    private fun createData3(index: Int) =
        Person("小明$index", Pet("小狗${index}号"))


}

