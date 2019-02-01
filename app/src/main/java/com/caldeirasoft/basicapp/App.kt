package com.caldeirasoft.basicapp

import android.app.Application
import com.caldeirasoft.basicapp.di.*
import com.caldeirasoft.basicapp.service.sync.SyncAdapterManager
import com.caldeirasoft.basicapp.service.sync.SyncHelper
import com.chibatching.kotpref.Kotpref
import com.github.salomonbrys.kodein.*
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.android.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        init310()
        initKoin()
        initKotpref()
        initLeakDetection()
        setErrorHandler()
        initPeriodicSync()
    }

    private fun init310()
    {
        AndroidThreeTen.init(this)
    }

    private fun initKoin() {
        startKoin(this, listOf(
                dataModule,
                networkModule,
                repositoryModule,
                usecaseModule,
                presentationModule
        ))
    }

    private fun initKotpref() {
        Kotpref.init(applicationContext)
    }

    private fun initLeakDetection() {
        if (BuildConfig.DEBUG) {
            //LeakCanary.install(this)
        }
    }

    private fun setErrorHandler() {
    }

    private fun initPeriodicSync() {
        val syncAdapterManager = SyncAdapterManager(this)
        syncAdapterManager.initPeriodicSync()
        SyncHelper.initPeriodicSync(this)
    }
}