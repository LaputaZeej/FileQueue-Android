package com.bugu.things.storage.bean

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.bugu.queue.bean._MqttMessage
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Author by xpl, Date on 2021/1/28.
 */

fun _MqttMessage.MqttMessage.print() = ("id = $id ,content = $content type = $type , time = $time")

fun getPath(context: Context, fileName: String): String {
    return context.filesDir.absolutePath + "/" + fileName
}

suspend fun FragmentActivity.suspendDialog(builder: AlertDialog.Builder.() -> Unit): Boolean =
    suspendCancellableCoroutine { continuation ->
        val alertDialog = AlertDialog.Builder(this)
            .setPositiveButton("确定") { _, _ ->
                continuation.resumeWith(Result.success(true))
            }
            .setNegativeButton("取消") { _, _ ->
                continuation.resumeWith(Result.success(false))
            }.apply {
                builder()
            }.create()
        continuation.invokeOnCancellation {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }