package com.caldeirasoft.basicapp.injection.module

import android.app.DownloadManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.caldeirasoft.basicapp.App
import com.caldeirasoft.basicapp.api.feedly.retrofit.FeedlyAPI
import com.caldeirasoft.basicapp.api.itunes.retrofit.ITunesAPI
import com.caldeirasoft.basicapp.data.db.AppDatabase
import com.caldeirasoft.basicapp.data.db.podcasts.PodcastDataSource
import com.caldeirasoft.basicapp.data.repository.PodcastRepository
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppModule(val application: App) {

    val bind = Kodein.Module {
        bind<Context>() with instance(application)
    }

    /*
    class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable?) {
            mainThreadHandler.post(command)
        }
    }

    companion object {
        val NETWORK_IO = Executors.newFixedThreadPool(5)
        val MAIN_THREAD = MainThreadExecutor()
        val DISK_IO = Executors.newSingleThreadExecutor()
    }

    //fun provideEventLogger(): EventLogger = EventLogger(FirebaseAnalytics.getInstance(application))
    fun getMainThreadExecutor():Executor = MAIN_THREAD

    fun getNetworkExecutor(): Executor = NETWORK_IO

    fun getDiskIoExecutor(): Executor = DISK_IO
    */
}