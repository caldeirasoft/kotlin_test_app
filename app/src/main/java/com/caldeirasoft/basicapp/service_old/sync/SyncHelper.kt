package com.caldeirasoft.basicapp.service_old.sync

import android.accounts.Account
import android.accounts.AccountManager
import android.content.*
import android.os.Bundle
import com.caldeirasoft.basicapp.R


/**
 * Created by Edmond on 23/03/2018.
 */
object SyncHelper {
    private const val ACCOUNT_NAME = "Event Sync"
    private const val INTERVAL = 1 * 1 * 60L // 1 minute
    //private const val INTERVAL = 1 * 60 * 60L // 1 hour

    @JvmStatic fun initPeriodicSync(context: Context) {
        //val account = Account(ACCOUNT_NAME, context.getString(R.string.account_type))
        val account = getAccount(context)
        ContentResolver.setSyncAutomatically(
                account,
                "content_authority",
                true)
        ContentResolver.addPeriodicSync(
                account,
                "content_authority",
                Bundle.EMPTY,
                INTERVAL
        )
    }

    @JvmStatic fun getAccount(context: Context): Account {
        val accountType = "com.caldeirasoft.testapp.sync" //TODO: context.getString(R.string.account_type)
        val account = Account(ACCOUNT_NAME, accountType)
        val accountManager = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
        accountManager.addAccountExplicitly(account, null, Bundle.EMPTY)
        return account

    }
}