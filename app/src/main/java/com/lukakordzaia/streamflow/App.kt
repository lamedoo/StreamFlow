package com.lukakordzaia.streamflow

import android.app.Application
import com.lukakordzaia.streamflow.di.generalModule
import com.lukakordzaia.streamflow.di.repositoryModule
import com.lukakordzaia.streamflow.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(listOf(repositoryModule, viewModelModule, generalModule))
        }
    }
}