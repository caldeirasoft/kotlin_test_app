package com.caldeirasoft.basicapp.injection.module

import com.caldeirasoft.basicapp.api.feedly.retrofit.FeedlyAPI
import com.caldeirasoft.basicapp.api.itunes.retrofit.ITunesAPI
import com.caldeirasoft.basicapp.api.rss.retrofit.RssAPI
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.provider
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class ApiModule()
{
    companion object {
        private val FEEDLY_BASE_URL = "https://cloud.feedly.com"
        private val ITUNES_BASE_URL = "https://itunes.apple.com"
    }

    val bind = Kodein.Module {
        bind<FeedlyAPI>() with provider { provideRetrofit(FEEDLY_BASE_URL).create(FeedlyAPI::class.java) }
        bind<ITunesAPI>() with provider { provideRetrofit(ITUNES_BASE_URL).create(ITunesAPI::class.java) }
    }

    fun provideRetrofit(baseURL: String) : Retrofit
    {
        val retrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit
    }
}