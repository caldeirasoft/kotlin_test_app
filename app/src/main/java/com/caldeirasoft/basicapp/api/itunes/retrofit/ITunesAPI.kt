package com.caldeirasoft.basicapp.api.itunes.retrofit

import com.caldeirasoft.basicapp.api.itunes.data.ResultId
import com.caldeirasoft.basicapp.api.itunes.data.SearchResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Edmond on 12/02/2018.
 */
interface ITunesAPI {
    @GET("search")
    fun search(@Query("media") mediaType:String, @Query("term") searchTerm:String):Call<SearchResult>

    //@GET("WebObjects/MZStoreServices.woa/ws/charts?g=26&Name=Podcasts")
    //fun top(@Query("cc") country:String, @Query("limit") limit:Int):Call<ResultId>

    @GET("WebObjects/MZStoreServices.woa/ws/charts?name=Podcasts")
    fun top(@Query("cc") country:String, @Query("limit") limit:Int, @Query("g") genre:Int = 26):Call<ResultId>

    @GET("lookup")
    fun lookup(@Query("id") resultIds: String):Call<SearchResult>
}