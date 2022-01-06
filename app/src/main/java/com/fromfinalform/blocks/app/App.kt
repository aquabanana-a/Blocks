package com.fromfinalform.blocks.app

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.fromfinalform.blocks.core.locale.LocaleUtils
import timber.log.Timber

class App : Application() {

    companion object {
        private var appContext: Context? = null

        fun getApplicationContext(): Context = appContext!!
        fun getResources(): Resources = appContext!!.resources
    }

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        initDagger()

        LocaleUtils.initLocale(this, "ru")
    }

    private fun initDagger() {

    }
}