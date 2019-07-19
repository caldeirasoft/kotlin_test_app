package com.caldeirasoft.basicapp.service_old.sync

import android.accounts.Account
import android.accounts.AccountManager
import android.content.*
import android.os.Bundle
import android.util.Log
import com.caldeirasoft.basicapp.R
import android.content.ContentResolver


/**
 * Created by Edmond on 23/03/2018.
 */
class SyncAdapterManager(context: Context) {
    companion object {
        private const val ACCOUNT_NAME = "Event Sync"
        private const val INTERVAL = 1 * 10 * 60L // 10 minutes
        private const val FLEX_TIME_IN_MINUTES = 5L // 5 minute
        private const val FLEX_TIME = FLEX_TIME_IN_MINUTES * 60L
        //private const val INTERVAL = 1 * 60 * 60L // 1 hour
    }

    private val TAG = SyncAdapterManager::class.java.simpleName
    private var authority:String
    private var type:String
    private var account:Account
    private var context:Context

    init {
        type = context.getString(R.string.account_type)
        authority = context.getString(R.string.content_authority)
        account = Account(ACCOUNT_NAME, this.type)
        this.context = context
    }

    fun initPeriodicSync() {
        Log.d(TAG, "initPeriodicSync called with updateConfigInterval = ${INTERVAL}")

        val accountManager = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
        if (accountManager.addAccountExplicitly(account, null, Bundle.EMPTY)) {
            setAccountSyncable()
            addPeriodicSync()
            ContentResolver.setSyncAutomatically(account, authority, true)
        }
        else {
            account = accountManager.getAccountsByType(type).first()
        }
    }

    fun cancelPeriodicSync()
    {
        Log.i(TAG, "Cancelling all periodic syncs")

        addAccount()

        setAccountSyncable()

        if (ContentResolver.getPeriodicSyncs(account, authority).size > 0)
            ContentResolver.cancelSync(account, authority)
    }

    fun syncImmediately()
    {
        val settingsBundle = Bundle()
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)

        val syncRequest = SyncRequest.Builder()
                .setSyncAdapter(account, authority)
                .setExtras(settingsBundle)
                .build()

        ContentResolver.requestSync(syncRequest)
    }

    private fun addPeriodicSync() {
        val settingsBundle = Bundle()
        SyncRequest.Builder()
                .syncPeriodic(INTERVAL, FLEX_TIME)
                .setSyncAdapter(account, authority)
                .setExtras(settingsBundle)
                .build()
        //ContentResolver.requestSync(syncRequest)
        ContentResolver.addPeriodicSync(account, authority, settingsBundle, INTERVAL)
    }

    private fun addAccount() {
        val accountManager = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
        accountManager.addAccountExplicitly(account, null, Bundle.EMPTY).let {
            if (!it)
                account = accountManager.getAccountsByType(type).first()
        }
    }

    private fun setAccountSyncable()
    {
        /*when (ContentResolver.getIsSyncable(account, authority)) {
            0 -> ContentResolver.setIsSyncable(account, authority, 1)
        }*/

        ContentResolver.setIsSyncable(account, authority, 1)
    }
}