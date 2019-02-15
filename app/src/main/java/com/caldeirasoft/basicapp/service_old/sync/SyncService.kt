package com.caldeirasoft.basicapp.service_old.sync

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Created by Edmond on 23/03/2018.
 */
class SyncService: Service() {
    companion object {
        private var syncAdapter: SyncAdapter? = null
        private val syncAdapterLock = Object()
    }

    override fun onCreate() {
        synchronized(syncAdapterLock) {
            syncAdapter?:let {
                syncAdapter = SyncAdapter(applicationContext, true)
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder =
            syncAdapter?.syncAdapterBinder!!
}