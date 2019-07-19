package com.caldeirasoft.castly.data.datasources.remote

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Created by Edmond on 12/02/2018.
 */
interface RssApi {
    @GET
    suspend fun getItems(@Url url:String): String;
}