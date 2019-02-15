package com.caldeirasoft.basicapp.service_old.sync

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.content.*
import android.os.Bundle

/**
 * Created by Edmond on 23/03/2018.
 */
class Authenticator(context: Context) : AbstractAccountAuthenticator(context)
{
    override fun addAccount(p0: AccountAuthenticatorResponse?, p1: String?, p2: String?, p3: Array<out String>?, p4: Bundle?): Bundle? = null

    override fun confirmCredentials(p0: AccountAuthenticatorResponse?, p1: Account?, p2: Bundle?): Bundle? = null

    override fun editProperties(p0: AccountAuthenticatorResponse?, p1: String?): Bundle {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getAuthToken(p0: AccountAuthenticatorResponse?, p1: Account?, p2: String?, p3: Bundle?): Bundle {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getAuthTokenLabel(p0: String?): String {
        throw UnsupportedOperationException("not implemented")
    }

    override fun hasFeatures(p0: AccountAuthenticatorResponse?, p1: Account?, p2: Array<out String>?): Bundle {
        throw UnsupportedOperationException("not implemented")
    }

    override fun updateCredentials(p0: AccountAuthenticatorResponse?, p1: Account?, p2: String?, p3: Bundle?): Bundle {
        throw UnsupportedOperationException("not implemented")
    }
}