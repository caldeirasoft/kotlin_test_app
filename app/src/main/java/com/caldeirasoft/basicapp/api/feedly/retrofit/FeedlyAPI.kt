package com.caldeirasoft.basicapp.api.feedly.retrofit

import com.caldeirasoft.basicapp.api.feedly.data.Entry
import com.caldeirasoft.basicapp.api.feedly.data.Feed
import com.caldeirasoft.basicapp.api.feedly.data.StreamEntries
import com.caldeirasoft.basicapp.api.feedly.data.StreamEntryIds
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Edmond on 12/02/2018.
 */
interface FeedlyAPI {
    @GET("v3/feeds/{feedId}")
    fun getFeed(@Path("feedId") feedId:String):Call<Feed>

    @POST("v3/feeds/.mget")
    fun getFeeds(@Body feeds:List<String>):Call<List<Feed>>

    @GET("v3/streams/{streamId}/contents")
    fun getStreamEntries(@Path("streamId") streamId:String, @Query("count") count:Int):Call<StreamEntries>

    @GET("v3/streams/{streamId}/contents")
    fun getStreamEntries(@Path("streamId") streamId:String, @Query("count") count:Int, @Query("continuation")continuation: String):Call<StreamEntries>

    @GET("v3/streams/{streamId}/contents")
    fun getStreamEntries(@Path("streamId") streamId:String, @Query("count") count:Int, @Query("newerThan")newerThan: Long, @Query("continuation")continuation: String):Call<StreamEntries>

    @GET("v3/streams/ids")
    fun getStreamEntryIds(@Query("streamId") streamId:String, @Query("count") count:Int, @Query("continuation")continuation: String?):Call<StreamEntryIds>

    @GET("v3/streams/ids")
    fun getStreamEntryIds(@Query("streamId") streamId:String, @Query("count") count:Int, @Query("newerThan")newerThan: Long?, @Query("continuation")continuation: String?):Call<StreamEntryIds>

    @GET("/v3/entries/{entryId}")
    fun getEntry(@Path("entryId") entryId:String):Call<Entry>

    @POST("/v3/entries/.mget")
    fun getEntries(@Body ids:List<String>):Call<List<Entry>>
}