package com.lukakordzaia.medootv

import android.app.Application
import com.lukakordzaia.medootv.di.generalModule
import com.lukakordzaia.medootv.di.repositoryModule
import com.lukakordzaia.medootv.di.viewModelModule
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