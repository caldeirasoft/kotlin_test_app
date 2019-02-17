package com.caldeirasoft.basicapp.service_old.sync

import android.app.Service
import android.content.*
import android.os.IBinder

/**
 * Created by Edmond on 23/03/2018.
 */
class AuthenticatorService : Service() {

    val mAuthenticator=Authenticator(applicationContext)
    override fun onBind(intent: Intent): IBinder? {
        return mAuthenticator.iBinder
    }
}