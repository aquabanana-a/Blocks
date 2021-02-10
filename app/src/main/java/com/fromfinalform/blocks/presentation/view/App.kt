/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

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