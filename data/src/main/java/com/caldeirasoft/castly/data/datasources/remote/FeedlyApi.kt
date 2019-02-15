package com.caldeirasoft.castly.data.datasources.remote

import com.caldeirasoft.castly.data.dto.feedly.EntryDto
import com.caldeirasoft.castly.data.dto.feedly.FeedDto
import com.caldeirasoft.castly.data.dto.feedly.StreamEntriesDto
import com.caldeirasoft.castly.data.dto.feedly.StreamEntryIdsDto
import kotlinx.coroutines.Deferred
import retrofit2.http.*

/**
 * Created by Edmond on 12/02/2018.
 */
interface FeedlyApi {
    @GET("v3/feeds/{feedId}")
    fun getFeed(@Path("feedId") feedId:String):Deferred<FeedDto>

    @POST("v3/feeds/.mget")
    fun getFeeds(@Body feeds:List<String>):Deferred<List<FeedDto>>

    @GET("v3/streams/{streamId}/contents")
    fun getStreamEntries(@Path("streamId") streamId:String, @Query("count") count:Int):Deferred<StreamEntriesDto>

    @GET("v3/streams/{streamId}/contents")
    fun getStreamEntries(@Path("streamId") streamId:String, @Query("count") count:Int, @Query("continuation")continuation: String):Deferred<StreamEntriesDto>

    @GET("v3/streams/{streamId}/contents")
    fun getStreamEntries(@Path("streamId") streamId:String, @Query("count") count:Int, @Query("newerThan")newerThan: Long, @Query("continuation")continuation: String):Deferred<StreamEntriesDto>

    @GET("v3/streams/ids")
    fun getStreamEntryIds(@Query("streamId") streamId:String, @Query("count") count:Int, @Query("continuation")continuation: String?):Deferred<StreamEntryIdsDto>

    @GET("v3/streams/ids")
    fun getStreamEntryIds(@Query("streamId") streamId:String, @Query("count") count:Int, @Query("newerThan")newerThan: Long?, @Query("continuation")continuation: String?):Deferred<StreamEntryIdsDto>

    @GET("/v3/entries/{entryId}")
    fun getEntry(@Path("entryId") entryId:String):Deferred<EntryDto>

    @POST("/v3/entries/.mget")
    fun getEntries(@Body ids:List<String>):Deferred<List<EntryDto>>
}