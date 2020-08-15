package com.caldeirasoft.basicapp

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.util.CoilUtils
import com.caldeirasoft.basicapp.di.*
import com.caldeirasoft.basicapp.presentation.utils.ThemeHelper
import com.caldeirasoft.basicapp.service_old.sync.SyncAdapterManager
import com.caldeirasoft.basicapp.service_old.sync.SyncHelper
import com.chibatching.kotpref.Kotpref
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.crashreporter.CrashReporterPlugin
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.navigation.NavigationFlipperPlugin
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.soloader.SoLoader
import com.facebook.stetho.Stetho
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.serialization.UnstableDefault
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    @FlowPreview
    @ExperimentalCoroutinesApi
    @UnstableDefault
    override fun onCreate() {
        super.onCreate()
        init310()
        initKotpref()
        initLeakDetection()
        initErrorHandler()
        initCoil()
        initStetho()
        initKoin()
        initPeriodicSync()
        setupTheme()
    }

    private fun init310() {
        AndroidThreeTen.init(this)
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    @UnstableDefault
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

    private fun initErrorHandler() {
        val exceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            //chuckerCollector.onError("CRASH", e)
            exceptionHandler?.uncaughtException(t, e)
        }
    }

    private fun initCoil() {
        Coil.setDefaultImageLoader(ImageLoader(applicationContext) {
            okHttpClient {
                okHttp().cache(CoilUtils.createDefaultCache(applicationContext))
                        .withChucker(applicationContext)
                        .build()
            }
        })
    }

    private fun initStetho() {
        Stetho.initializeWithDefaults(this)
    }

    private fun initLeakDetection() {
        if (BuildConfig.DEBUG) {
            //LeakCanary.install(this)
        }
    }

    private fun setupTheme() {
        ThemeHelper.applyTheme(ThemeHelper.LIGHT_MODE)
    }

    private fun initPeriodicSync() {
        /*val syncAdapterManager = SyncAdapterManager(this)
        syncAdapterManager.initPeriodicSync()
        SyncHelper.initPeriodicSync(this)*/
    }
}