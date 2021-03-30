package com.bugu.things.storage.retrofit

import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import com.bugu.things.storage.bean.A
import com.bugu.things.storage.bean.B
import com.bugu.things.storage.bean.MQMessage
import com.google.gson.Gson
import java.lang.StringBuilder
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.net.URL
import kotlin.reflect.jvm.kotlinFunction

/**
 * Author by xpl, Date on 2021/2/8.
 */
interface HttpFunctions {
    /**
     * 获取玩安卓的json数据
     * @param cid 这个接口的参数(虽然不知道有什么用emmm)
     */
    fun getJson(
        _callback: ObserverCallBack?,
        cid: String
    ): MQMessage<A<List<B>>>

    companion object {
        val instance: HttpFunctions = getHttpFunctions()

        private fun getHttpFunctions(): HttpFunctions {
            val clz = HttpFunctions::class.java
            return Proxy.newProxyInstance(
                clz.classLoader,
                arrayOf(clz),
                HttpFunctionsHandler()
            ) as HttpFunctions
        }
    }

    class HttpFunctionsHandler : InvocationHandler {
        private val handler = Handler(Looper.getMainLooper())
        private val gson: Gson by lazy { Gson() }

        override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any {
            "========method========".print()
            method?.run {
                "   打印方法名".print()
                name.print()

                "   打印方法参数名".print()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    parameters.forEach {
                        it.name.print()
                    }
                }
                "   打印方法返回名".print()
                returnType.run {
                    name.print()
                    this.typeParameters.forEach {
                        it.name.print()
                        it.typeName.print()

//                        it.bounds.forEach {
//                            it.typeName.print()
//                        }
//                        it.genericDeclaration.name.print()
                    }
                }

                "   kt反射打印方法参数名".print()
                kotlinFunction?.parameters?.forEach {
                    "${it.type} -> ${it.name}".print()
                }
                "   kt反射打印方法返回名".print()
                kotlinFunction?.returnType?.run {
                    this.toString().print()
                }
            }
            "========args========".print()
            args?.forEach { it.print() }

            "========start========".print()
            val kotlinFunction = method?.kotlinFunction
            val url = StringBuilder(HttpConfig.ROOT_URL).append("article/list/0/json?")
            var callBack: ObserverCallBack? = null
            kotlinFunction?.parameters?.forEachIndexed { index, p ->
                when (p.name) {
                    null -> {
                    }
                    "_callback" -> {
                        callBack = args?.get(index - 1) as? ObserverCallBack
                    }
                    else -> {
                        url.append(p.name)
                            .append("=")
                            .append(args?.get(index - 1))
                            .append("&")
                    }
                }
            }
            if (url.endsWith('&'))
                url.deleteCharAt(url.length - 1)//清除最后一个&
            url.print()
            val data = URL(url.toString()).readText()
            handler.post {
                callBack?.handleResult(data, 200, 200)
            }

            val message = MQMessage<A<List<B>>>().apply {
                this.data = A<List<B>>(listOf(B("b1"), B("B2"), B("B3")))
                this.index = 1
                this.time = System.currentTimeMillis()
                this.type = 0
            }
            val json = gson.toJson(message)
            val returnType = method?.returnType
            if (returnType != null) {
                return gson.fromJson(json, returnType)
            }
            /* when (returnType) {
                 String::class.java -> {
                     val newInstance = returnType.newInstance()
                     return newInstance + "返回值"
                 }
                 MQMessage::class.java -> {
                     val fromJson = gson.fromJson(json, returnType)
                     return fromJson
                 }
                 else -> {

                 }
             }*/
            return Unit
        }

    }

}