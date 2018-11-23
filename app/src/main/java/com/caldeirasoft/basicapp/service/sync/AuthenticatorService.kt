package com.caldeirasoft.basicapp.service.sync

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.app.Service
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast

/**
 * Created by Edmond on 23/03/2018.
 */
class AuthenticatorService : Service() {

    val mAuthenticator=Authenticator(applicationContext)
    override fun onBind(intent: Intent): IBinder? {
        return mAuthenticator.iBinder
    }
}