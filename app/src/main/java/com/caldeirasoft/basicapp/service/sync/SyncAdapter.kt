package com.caldeirasoft.basicapp.service.sync

import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.os.Bundle
import android.util.Log
import android.widget.Toast

/**
 * Created by Edmond on 23/03/2018.
 */
class SyncAdapter(context: Context, autoInitialize: Boolean)
    : AbstractThreadedSyncAdapter(context, autoInitialize) {

    override fun onPerformSync(p0: Account?, p1: Bundle?, p2: String?, p3: ContentProviderClient?, p4: SyncResult?) {
        /* we can do things like
              1) downloading data from a server
              2) Uploading data to server
              3) be sure to handle network related exceptions.
            */
        // For exapmple i am connecting to a server
        Unit
        Log.i(TAG, "onPerformSync() was called")
        //Toast.makeText(context,"Syncing",Toast.LENGTH_SHORT)

    }

    companion object {
        private val TAG = SyncAdapter::class.java.simpleName
    }
}