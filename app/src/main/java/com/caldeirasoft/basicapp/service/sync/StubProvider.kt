package com.caldeirasoft.basicapp.service.sync

import android.accounts.Account
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast

/**
 * Created by Edmond on 23/03/2018.
 */
class StubProvider : ContentProvider()
{
    override fun insert(p0: Uri?, p1: ContentValues?): Uri? = null

    override fun query(p0: Uri?, p1: Array<out String>?, p2: String?, p3: Array<out String>?, p4: String?): Cursor? = null

    override fun onCreate(): Boolean = true

    override fun update(p0: Uri?, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int = 0

    override fun delete(p0: Uri?, p1: String?, p2: Array<out String>?): Int = 0

    override fun getType(p0: Uri?): String? = null
}