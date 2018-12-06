package com.caldeirasoft.basicapp.util

import android.util.Log
import okhttp3.logging.HttpLoggingInterceptor
import java.util.*
import java.util.logging.Logger

class HttpLogger : HttpLoggingInterceptor.Logger {

    override fun log(message: String) {
        Log.d(buildTag(tag), message)
    }

    companion object {
        private const val tag = "HttpLogger"

        private fun buildTag(tag: String): String {
            return String.format(Locale.ROOT, "|%s|%s|%s|", tag, Thread.currentThread().name, Math.random())
        }
    }
}