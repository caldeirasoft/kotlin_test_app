package com.caldeirasoft.basicapp.util.interceptors

import android.content.Context
import com.caldeirasoft.basicapp.util.network.NetworkStatusUtil
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Okhttp Interceptor used to add the Cache-Control header.
 * Currently caches for 10 min if network is available, 1 day if offline
 *
 * @since 1.0
 */
class RewriteOfflineRequestInterceptor(val context: Context) : Interceptor {

    val networkStatusUtil = NetworkStatusUtil(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if (request.method.equals("GET")) {
            if (!networkStatusUtil.hasNetConnection()) {
                request = request.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "only-if-cached")
                        .build()
            } else {
                //Use stale cache thats at the most 1 day old if no network available
                request = request.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "max-stale=86400")
                        .build()
            }
        }

        return chain.proceed(request)
    }
}