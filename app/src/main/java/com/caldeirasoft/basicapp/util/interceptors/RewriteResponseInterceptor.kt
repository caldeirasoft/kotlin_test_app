package com.caldeirasoft.basicapp.util.interceptors

import android.content.Context
import com.caldeirasoft.basicapp.util.network.NetworkStatusUtil
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Okhttp Interceptor used to add the Cache-Control header.
 * Currently caches for 20 min if network is available, 1 day if offline
 *
 * @since 1.0
 */
class RewriteResponseInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val originalResponse = chain.proceed(request)
        val cacheControl = originalResponse.header("CacheControl")
        return if (cacheControl == null ||
                arrayListOf("no-store", "no-cache", "must-revalidate", "max-age=0").any { cacheControl.contains(it) }) {
            //Cache results for 20 min
            originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, max-age=" + 600)
                    .build()
        } else {
            originalResponse
        }
    }
}