package com.lukakordzaia.streamflow

import android.app.Application
import com.lukakordzaia.core.di.generalModule
import com.lukakordzaia.core.di.repositoryModule
import com.lukakordzaia.streamflow.di.phoneViewModelModule
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
            modules(listOf(repositoryModule, phoneViewModelModule, generalModule))
        }
    }
}