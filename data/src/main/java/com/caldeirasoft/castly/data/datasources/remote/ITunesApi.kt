package com.caldeirasoft.castly.data.datasources.remote

import com.caldeirasoft.castly.data.dto.itunes.*
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Edmond on 12/02/2018.
 */
interface ITunesApi {
    companion object {
        internal const val baseUrl = "https://itunes.apple.com"
    }

    /**
     * Search podcast by term
     */
    @GET("search")
    suspend fun search(@Query("media") mediaType:String, @Query("term") searchTerm:String): SearchResultDto

    //@GET("WebObjects/MZStoreServices.woa/ws/charts?g=26&Name=Podcasts")
    //fun top(@Query("cc") country:String, @Query("limit") limit:Int):Call<ResultIdDto>

    /**
     * Search podcast by term
     */
    @GET("search?media=podcast&term=podcast")
    suspend fun topPodcasts(@Query("cc") country:String, @Query("limit") limit:Int, @Query("g") genre:Int = 26): SearchResultDto

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


    /**
     * Podcasts collections by genre
     */
    @GET("/genre/id{genre}")
    suspend fun genre(@Header("X-Apple-Store-Front") storeFront:String, @Path("genre") genre:Int = 26): GenreResultDto

    /**
     * Podcasts collections by genre
     */
    @GET("/collection")
    suspend fun collection(@Header("X-Apple-Store-Front") storeFront:String, @Query("fcId") id:Int): CollectionResultDto

    /**
     * View multiroom
     */
    @GET("WebObjects/MZStore.woa/wa/viewMultiRoom")
    suspend fun viewMultiRoom(@Header("X-Apple-Store-Front") storeFront:String, @Query("fcId") id:Int): MultiRoomResultDto

    /**
     * View feature room
     */
    @GET("WebObjects/MZStore.woa/wa/viewFeature")
    suspend fun viewFeature(@Header("X-Apple-Store-Front") storeFront:String, @Query("id") id:Int): MultiRoomResultDto


    /**
     * Lookup podcast info from ID
     */
    @GET("lookup")
    suspend fun lookup(@Query("id") resultIds: String): SearchResultDto
}