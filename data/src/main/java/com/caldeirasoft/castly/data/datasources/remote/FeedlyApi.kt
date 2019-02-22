package com.caldeirasoft.castly.data.datasources.remote

import com.caldeirasoft.castly.data.dto.feedly.EntryDto
import com.caldeirasoft.castly.data.dto.feedly.FeedDto
import com.caldeirasoft.castly.data.dto.feedly.StreamEntriesDto
import com.caldeirasoft.castly.data.dto.feedly.StreamEntryIdsDto
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Edmond on 12/02/2018.
 */
interface FeedlyApi {
    @GET("v3/feeds/{feedId}")
    fun getFeed(@Path("feedId") feedId:String): Call<FeedDto>

    @POST("v3/feeds/.mget")
    fun getFeeds(@Body feeds:List<String>):List<FeedDto>

    @GET("v3/streams/{streamId}/contents")
    fun getStreamEntries(@Path("streamId") streamId:String, @Query("count") count:Int):Call<StreamEntriesDto>

    @GET("v3/streams/{streamId}/contents")
    fun getStreamEntries(@Path("streamId") streamId:String, @Query("count") count:Int, @Query("continuation")continuation: String): Call<StreamEntriesDto>

    @GET("v3/streams/{streamId}/contents")
    fun getStreamEntries(@Path("streamId") streamId:String, @Query("count") count:Int, @Query("newerThan")newerThan: Long, @Query("continuation")continuation: String): Call<StreamEntriesDto>

    @GET("v3/streams/ids")
    fun getStreamEntryIds(@Query("streamId") streamId:String, @Query("count") count:Int, @Query("continuation")continuation: String?): Call<StreamEntryIdsDto>

    @GET("v3/streams/ids")
    fun getStreamEntryIds(@Query("streamId") streamId:String, @Query("count") count:Int, @Query("newerThan")newerThan: Long?, @Query("continuation")continuation: String?): Call<StreamEntryIdsDto>

    @GET("/v3/entries/{entryId}")
    fun getEntry(@Path("entryId") entryId:String): Call<EntryDto>

    @POST("/v3/entries/.mget")
    fun getEntries(@Body ids:List<String>): Call<List<EntryDto>>
}