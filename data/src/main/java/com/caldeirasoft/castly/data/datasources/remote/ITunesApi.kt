package com.caldeirasoft.castly.data.datasources.remote

import com.caldeirasoft.castly.data.dto.itunes.ResultIdDto
import com.caldeirasoft.castly.data.dto.itunes.SearchResultDto
import com.caldeirasoft.castly.data.dto.itunes.StoreResultDto
import kotlinx.coroutines.Deferred
import retrofit2.http.*

/**
 * Created by Edmond on 12/02/2018.
 */
interface ITunesApi {
    /**
     * Search podcast by term
     */
    @GET("search")
    suspend fun search(@Query("media") mediaType:String, @Query("term") searchTerm:String): SearchResultDto

    //@GET("WebObjects/MZStoreServices.woa/ws/charts?g=26&Name=Podcasts")
    //fun top(@Query("cc") country:String, @Query("limit") limit:Int):Call<ResultIdDto>

    /**
     * Top podcasts by genre
     */
    @GET("WebObjects/MZStoreServices.woa/ws/charts?name=Podcasts")
    suspend fun top(@Query("cc") country:String, @Query("limit") limit:Int, @Query("g") genre:Int = 26): ResultIdDto

    /**
     * Top podcasts by genre
     */
    @GET("WebObjects/MZStore.woa/wa/viewGrouping?id=78")
    suspend fun viewGrouping(@Header("X-Apple-Store-Front") storeFront:String): StoreResultDto


    @Headers("X-Apple-Store-Front: 143442-3,30")
    @GET("/WebObjects/MZStore.woa/wa/viewGrouping?id=78")
    suspend fun viewGrouping2(): String;

    /**
     * Lookup podcast info from ID
     */
    @GET("lookup")
    suspend fun lookup(@Query("id") resultIds: String): SearchResultDto
}