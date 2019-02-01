package com.caldeirasoft.basicapp.data.datasources.remote

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Created by Edmond on 12/02/2018.
 */
interface RssApi {
    @GET
    fun getItems(@Url url:String): Deferred<String>;
}