package com.fromfinalform.blocks.presentation.view

import android.app.Application
import android.content.Context
import android.content.res.Resources

class App : Application() {

    companion object {
        private var appContext: Context? = null

        fun getApplicationContext(): Context = appContext!!
        fun getResources(): Resources = appContext!!.resources
    }

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext
    }
}