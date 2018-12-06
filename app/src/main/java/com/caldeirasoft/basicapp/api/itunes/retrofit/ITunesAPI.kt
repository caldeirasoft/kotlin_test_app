package com.caldeirasoft.basicapp.api.itunes.retrofit

import com.caldeirasoft.basicapp.api.itunes.data.ResultId
import com.caldeirasoft.basicapp.api.itunes.data.SearchResult
import com.caldeirasoft.basicapp.api.itunes.data.StoreResult
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Edmond on 12/02/2018.
 */
interface ITunesAPI {
    /**
     * Search podcast by term
     */
    @GET("search")
    fun search(@Query("media") mediaType:String, @Query("term") searchTerm:String):Call<SearchResult>

    //@GET("WebObjects/MZStoreServices.woa/ws/charts?g=26&Name=Podcasts")
    //fun top(@Query("cc") country:String, @Query("limit") limit:Int):Call<ResultId>

    /**
     * Top podcasts by genre
     */
    @GET("WebObjects/MZStoreServices.woa/ws/charts?name=Podcasts")
    fun top(@Query("cc") country:String, @Query("limit") limit:Int, @Query("g") genre:Int = 26):Call<ResultId>

    /**
     * Top podcasts by genre
     */
    @GET("WebObjects/MZStore.woa/wa/viewGrouping?id=78")
    fun viewGrouping(@Header("X-Apple-Store-Front") storeFront:String):Call<StoreResult>


    @Headers("X-Apple-Store-Front: 143442-3,30")
    @GET("/WebObjects/MZStore.woa/wa/viewGrouping?id=78")
    fun viewGrouping2(): Call<String>;

    /**
     * Lookup podcast info from ID
     */
    @GET("lookup")
    fun lookup(@Query("id") resultIds: String):Call<SearchResult>
}