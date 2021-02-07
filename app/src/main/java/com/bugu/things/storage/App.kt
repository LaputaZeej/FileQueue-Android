package com.bugu.things.storage

import android.app.Application
import android.content.Context

/**
 * Author by xpl, Date on 2021/2/6.
 */

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }


    companion object {
        @JvmStatic
        var INSTANCE: Context? = null
    }
}