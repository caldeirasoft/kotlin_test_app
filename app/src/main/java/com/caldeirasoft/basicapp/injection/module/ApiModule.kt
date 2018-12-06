package com.caldeirasoft.basicapp.injection.module

import android.os.Build
import com.caldeirasoft.basicapp.BuildConfig
import com.caldeirasoft.basicapp.api.feedly.retrofit.FeedlyAPI
import com.caldeirasoft.basicapp.api.itunes.retrofit.ITunesAPI
import com.caldeirasoft.basicapp.api.rss.retrofit.RssAPI
import com.caldeirasoft.basicapp.util.HttpLogger
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.provider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

class ApiModule()
{
    companion object {
        private val FEEDLY_BASE_URL = "https://cloud.feedly.com"
        private val ITUNES_BASE_URL = "https://itunes.apple.com"

        private const val DEFAULT_CONN_TIME_OUT = 30000
        private const val DEFAULT_READ_TIME_OUT = 30000
        private const val DEFAULT_WRITE_TIME_OUT = 30000

    }

    val bind = Kodein.Module {
        bind<FeedlyAPI>() with provider { provideRetrofit(FEEDLY_BASE_URL).create(FeedlyAPI::class.java) }
        bind<ITunesAPI>() with provider { provideRetrofit(ITUNES_BASE_URL).create(ITunesAPI::class.java) }
    }

    fun provideRetrofit(baseURL: String) : Retrofit
    {
        val httpClientBuilder = OkHttpClient.Builder().apply {

            //set time out
            connectTimeout(DEFAULT_CONN_TIME_OUT.toLong(), TimeUnit.MILLISECONDS)
            readTimeout(DEFAULT_READ_TIME_OUT.toLong(), TimeUnit.MILLISECONDS)
            writeTimeout(DEFAULT_WRITE_TIME_OUT.toLong(), TimeUnit.MILLISECONDS)

            if (BuildConfig.DEBUG) {
                //set logging interceptor
                HttpLoggingInterceptor(HttpLogger()).let {
                    it.level = HttpLoggingInterceptor.Level.HEADERS
                    addInterceptor(it)
                }
            }
        }

        // retrofit
        val retrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .client(httpClientBuilder.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        return retrofit
    }
}