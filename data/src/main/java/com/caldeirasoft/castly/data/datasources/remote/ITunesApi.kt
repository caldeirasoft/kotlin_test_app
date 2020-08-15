package com.caldeirasoft.castly.data.datasources.remote

import com.caldeirasoft.castly.data.dto.itunes.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by Edmond on 12/02/2018.
 */
interface ITunesApi {
    companion object {
        internal const val baseUrl = "https://itunes.apple.com"
    }

    /**
     * Podcasts collections by genre
     */
    @GET("/genre/id{genre}")
    @Headers(
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json")
    suspend fun genre(@Header("X-Apple-Store-Front") storeFront: String, @Path("genre") genre: Int = 26): Response<GroupingPageResultDto>

    /**
     * Search podcast by term
     */
    @GET("search")
    suspend fun search(@Query("media") mediaType: String, @Query("term") searchTerm: String): SearchResultDto

    //@GET("WebObjects/MZStoreServices.woa/ws/charts?g=26&Name=Podcasts")
    //fun top(@Query("cc") country:String, @Query("limit") limit:Int):Call<ResultIdDto>

    /**
     * Search podcast by term
     */
    @GET("search?media=podcast&term=podcast")
    @Headers(
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    )
    suspend fun topPodcasts(@Query("cc") country: String, @Query("limit") limit: Int, @Query("g") genre: Int = 26): Response<SearchResultDto>

    /**
     * Top podcasts by genre
     */
    @GET("WebObjects/MZStoreServices.woa/ws/charts?name=Podcasts")
    @Headers(
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    )
    suspend fun top(@Query("cc") country: String, @Query("limit") limit: Int, @Query("g") genre: Int = 26): Response<ResultIdDto>

    /**
     * Get grouping page
     */
    @GET("WebObjects/MZStore.woa/wa/viewGrouping?id=78")
    suspend fun viewGrouping(@Header("X-Apple-Store-Front") storeFront:String): StoreResultDto

    /**
     * Get room page
     */
    @GET("{roomUrl}")
    @Headers(
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    )
    suspend fun viewRoom(@Header("X-Apple-Store-Front") storeFront: String, @Path("roomUrl") roomUrl: String): RoomResultDto

    /**
     * Get room page
     */
    @GET("{multiRoomUrl}")
    @Headers(
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    )
    suspend fun viewMultiRoom(@Header("X-Apple-Store-Front") storeFront: String, @Path("multiRoomUrl") multiRoomUrl: String): RoomResultDto

    /**
     * Artist
     */
    @GET("WebObjects/DZR.woa/wa/viewArtist")
    @Headers(
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    )
    suspend fun artist(@Header("X-Apple-Store-Front") storeFront: String, @Query("id") artistId: Long): ArtistResultDto

    /**
     * Lookup podcast info from ID
     */
    @GET("lookup")
    @Headers(
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    )
    suspend fun lookup(@Query("id") resultIds: String): Response<SearchResultDto>
}