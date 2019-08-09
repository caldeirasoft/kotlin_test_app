package com.caldeirasoft.basicapp.util

/**
 * Eases the Fragment.newInstance ceremony by marking the fragment's args with this delegate
 * Just write the property in newInstance and read it like any other property after the fragment has been created
 *
 * Inspired by Adam Powell, he mentioned it during his IO/17 talk about Kotlin
 */
object Constants {
    val FEEDLY_BASE_URL = "https://cloud.feedly.com"
    val ITUNES_BASE_URL = "https://itunes.apple.com"
    val PODCASTS_BASE_URL = "https://podcasts.apple.com"

    const val DEFAULT_CONN_TIME_OUT = 10000
    const val DEFAULT_READ_TIME_OUT = 10000
    const val DEFAULT_WRITE_TIME_OUT = 10000

    const val DEFAULT_CACHE_SIZE = 32L * 1024L * 1024L // 32 MiB
}