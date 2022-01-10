package com.fromfinalform.blocks.di

import com.fromfinalform.blocks.app.App
import com.fromfinalform.blocks.core.di.ApplicationScope
import com.fromfinalform.blocks.core.di.Dependencies
import android.app.Application
import dagger.BindsInstance
import dagger.Component

//@ApplicationScope
//@Component(
//    modules = [
//        AppModule::class
//    ],
//    dependencies = [
//        //NetworkDependenciesProvider::class
//    ]
//)
//interface AppComponent :
//    ApplicationProvider,
//    //NetworkDependenciesProvider,
//    Dependencies {
//
//    @Component.Factory
//    interface Factory {
//        fun create(
//            @BindsInstance application: Application,
//            //networkDependenciesProvider: NetworkDependenciesProvider
//        ): AppComponent
//    }
//
//    fun inject(app: App)
//}