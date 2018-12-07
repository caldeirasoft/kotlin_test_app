package com.caldeirasoft.basicapp

import android.app.Application
import com.caldeirasoft.basicapp.injection.module.ApiModule
import com.caldeirasoft.basicapp.injection.module.AppModule
import com.caldeirasoft.basicapp.injection.module.RoomDbModule
import com.caldeirasoft.basicapp.service.sync.SyncAdapterManager
import com.caldeirasoft.basicapp.service.sync.SyncHelper
import com.chibatching.kotpref.Kotpref
import com.github.salomonbrys.kodein.*
import com.jakewharton.threetenabp.AndroidThreeTen

class App : Application(), KodeinAware {
    override lateinit var kodein: Kodein

    override fun onCreate() {
        super.onCreate()
        App.context = this
        init310()
        initKodein()
        initKotpref()
        initLeakDetection()
        setErrorHandler()
        initPeriodicSync()
    }

    private fun init310()
    {
        AndroidThreeTen.init(this)
    }

    private fun initKodein() {
        kodein = Kodein {
            constant("db") with "data.db"
            import(AppModule(this@App).bind)
            import(ApiModule().bind)
            import(RoomDbModule().bind)
            //import(MediaModule().bind)
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

    private fun setErrorHandler() {
    }

    private fun initPeriodicSync() {
        val syncAdapterManager = SyncAdapterManager(this)
        syncAdapterManager.initPeriodicSync()
        SyncHelper.initPeriodicSync(this)
    }

    companion object {
        @JvmField
        var context: App? = null
    }
}