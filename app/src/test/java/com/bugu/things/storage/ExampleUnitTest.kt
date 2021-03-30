package com.bugu.things.storage

import com.bugu.things.storage.bean.Person
import com.bugu.things.storage.bean.Pet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Test
import java.math.BigDecimal
import kotlin.math.roundToInt

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun t1() {
        // ok
        val json = createJson()
        val take = B().take<Person<Pet>>(json)
        println(take)
    }

    @Test
    fun t2() {
        // ok
        val json = createJson()
        val take = B().take<Person<Pet>>(json, object : TypeToken<Person<Pet>>() {})
        println(take)
    }

    @Test
    fun t3() {
        val gson = Gson()
        val person = Person("小明", Pet("小狗1号"))
        val json = gson.toJson(Box(person))
        val take = B().takeBox<Person<Pet>>(json)
        println(take)
    }

    @Test
    fun t4() {
        val json = createJson()
        val take = DD().apply {
            c = C<Person<Pet>>()
        }.take<Person<Pet>>(json)
        println(take)
    }

    @Test
    fun t6() {
        SingletonA.getInstance().test("1")
        SingletonA.getInstance().test(1)
        SingletonA.getInstance().test(1f)
        SingletonA.getInstance().test(listOf<Int>())
    }

    @Test
    fun t7() {

        test(0.59f)
        test(1000000.04f)

    }

    fun test(value: Float) {
        val ratio = 100f
        val result = value * ratio
        println(result)
        println(result.roundToInt())
        println("======================")

        val s = BigDecimal(value.toString()).times(BigDecimal(ratio.toString()))
        println("s = ${s}")
        println("s = ${s.toFloat()}")
        println("s = ${s.toInt()}")
        println("s = ${s.intValueExact()}")

        // 1 2
        // 3 4 5 6 7 8 9 0
    }
}

private fun createJson(): String {
    val gson = Gson()
    val person = Person("小明", Pet("小狗1号"))
    val json = gson.toJson(person)
    return json
}

class B {
    val gson: Gson = Gson()

    inline fun <reified T> take(json: String): T {
        return gson.fromJson(json, object : TypeToken<T>() {}.type)
    }

    fun <T> take(json: String, token: TypeToken<T>): T {
        return gson.fromJson(json, token.type)
    }

    fun <T> takeBox(json: String): T {
        val s = gson.fromJson<Box<T>>(json, object : TypeToken<Box<T>>() {}.type)
        return s.data
    }
}

data class Box<T>(val data: T)

class C<T> {
    val gson: Gson = Gson()

    fun take(json: String, token: TypeToken<T>): T {
        return gson.fromJson(json, token.type)
    }

    fun take(json: String): T {
        return gson.fromJson(json, object : TypeToken<T>() {}.type)
    }
}

class DD() {
    var c: C<*>? = null

    fun <T> take(json: String): T? {
        val real = c as? C<T>
        val take = real?.take(json)
        return take
    }
}



