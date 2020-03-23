package com.caldeirasoft.basicapp

import android.app.Application
import com.caldeirasoft.basicapp.di.*
import com.caldeirasoft.basicapp.presentation.utils.ThemeHelper
import com.caldeirasoft.basicapp.service_old.sync.SyncAdapterManager
import com.caldeirasoft.basicapp.service_old.sync.SyncHelper
import com.chibatching.kotpref.Kotpref
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        init310()
        initKoin()
        initKotpref()
        initLeakDetection()
        setErrorHandler()
        initPeriodicSync()
        setupTheme()
    }

    private fun init310()
    {
        AndroidThreeTen.init(this)
    }

    private fun initKoin() {
        startKoin {
            // use AndroidLogger as Koin Logger - default Level.INFO
            androidLogger()

            // use the Android context given there
            androidContext(this@App)

            // module list
            modules(
                    appModule,
                    dataModule,
                    networkModule,
                    repositoryModule,
                    usecaseModule,
                    mediaModule,
                    presentationModule
            )
        }
    }

    private fun initKotpref() {
        Kotpref.init(applicationContext)
    }

    private fun initLeakDetection() {
        if (BuildConfig.DEBUG) {
            //LeakCanary.install(this)
        }
    }

    private fun setupTheme() {
        ThemeHelper.applyTheme(ThemeHelper.LIGHT_MODE)
    }

    private fun setErrorHandler() {
    }

    private fun initPeriodicSync() {
        val syncAdapterManager = SyncAdapterManager(this)
        syncAdapterManager.initPeriodicSync()
        SyncHelper.initPeriodicSync(this)
    }
}