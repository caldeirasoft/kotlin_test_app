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
    companion object {
        internal const val baseUrl = "https://cloud.feedly.com"
    }

    @GET("v3/feeds/{feedId}")
    suspend fun getFeed(@Path("feedId") feedId:String): FeedDto

    @POST("v3/feeds/.mget")
    suspend fun getFeeds(@Body feeds:List<String>):List<FeedDto>

    @GET("v3/streams/{streamId}/contents")
    suspend fun getStreamEntries(@Path("streamId") streamId:String, @Query("count") count:Int): StreamEntriesDto

    @GET("v3/streams/{streamId}/contents")
    suspend fun getStreamEntries(@Path("streamId") streamId:String, @Query("count") count:Int, @Query("continuation")continuation: String): StreamEntriesDto

    @GET("v3/streams/{streamId}/contents")
    suspend fun getStreamEntries(@Path("streamId") streamId:String, @Query("count") count:Int, @Query("newerThan")newerThan: Long, @Query("continuation")continuation: String): StreamEntriesDto

    @GET("v3/streams/ids")
    suspend fun getStreamEntryIds(@Query("streamId") streamId:String, @Query("count") count:Int, @Query("continuation")continuation: String?): StreamEntryIdsDto

    @GET("v3/streams/ids")
    suspend fun getStreamEntryIds(@Query("streamId") streamId:String, @Query("count") count:Int, @Query("newerThan")newerThan: Long?, @Query("continuation")continuation: String?): StreamEntryIdsDto

    @GET("/v3/entries/{entryId}")
    suspend fun getEntry(@Path("entryId") entryId:String): EntryDto

    @POST("/v3/entries/.mget")
    suspend fun getEntries(@Body ids:List<String>): List<EntryDto>
}