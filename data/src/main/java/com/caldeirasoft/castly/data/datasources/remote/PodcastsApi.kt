package com.caldeirasoft.castly.data.datasources.remote

import com.caldeirasoft.castly.data.dto.itunes.*
import kotlinx.coroutines.Deferred
import retrofit2.http.*

/**
 * Created by Edmond on 12/02/2018.
 */
interface PodcastsApi {
    companion object {
        internal const val baseUrl = "https://podcasts.apple.com"
    }

    /**
     * Podcasts collections by genre
     */
    @GET("/podcast/id{id}")
    suspend fun podcast(@Header("X-Apple-Store-Front") storeFront:String, @Path("id") id:Long): PodcastResultDto
}