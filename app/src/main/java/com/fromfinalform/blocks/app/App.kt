package com.fromfinalform.blocks.app

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.fromfinalform.blocks.core.di.DependenciesProvider
import com.fromfinalform.blocks.core.di.HasComponentDependencies
import com.fromfinalform.blocks.core.locale.LocaleUtils
//import com.fromfinalform.blocks.di.AppComponent
//import com.fromfinalform.blocks.di.DaggerAppComponent

class App : Application(), HasComponentDependencies {

    companion object {
//        @JvmStatic
//        lateinit var component: AppComponent

        private var appContext: Context? = null

        fun getApplicationContext(): Context = appContext!!
        fun getResources(): Resources = appContext!!.resources
    }

    // TODO: add inject after adding at least 1 feature component
    override lateinit var dependencies: DependenciesProvider
        //Dagger does not support injection into private fields
        protected set

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        initDagger()

        LocaleUtils.initLocale(this, "ru")
    }

    private fun initDagger() {
//        val networkDependenciesProvider: NetworkDependenciesProvider =
//            NetworkComponent.Companion.init(this)

//        component = DaggerAppComponent.factory()
//            .create(
//                application = this,
//                //networkDependenciesProvider = networkDependenciesProvider
//            )
//        component.inject(this)
    }
}