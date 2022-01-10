package com.fromfinalform.blocks.di

import com.fromfinalform.blocks.core.di.ApplicationScope
import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides

//private const val PREFERENCES_STORE_NAME = "blocks.prefs.store"
//
//@Module
//interface AppModule {
//
//    companion object {
//        @Provides
//        @ApplicationScope
//        fun provideContext(app: Application): Context = app.applicationContext
//
//        @Provides
//        @ApplicationScope
//        fun provideDatastore(app: Application): DataStore<Preferences> =
//            PreferenceDataStoreFactory.create(
//                produceFile = {
//                    app.preferencesDataStoreFile(PREFERENCES_STORE_NAME)
//                }
//            )
//    }
//}