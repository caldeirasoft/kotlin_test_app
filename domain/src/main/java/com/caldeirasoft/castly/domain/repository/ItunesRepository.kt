package com.caldeirasoft.castly.domain.repository;

import com.caldeirasoft.castly.domain.util.FlowPaginationModel
import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.domain.model.itunes.*
import com.caldeirasoft.castly.domain.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

/**
 * Created by Edmond on 15/02/2018.
 */

@FlowPreview
@ExperimentalCoroutinesApi
interface ItunesRepository {

    /**
     * Get store data by genre (grouping_page)
     */
    fun getGroupingPageData(storeFront: String, genreId: Int): Flow<Resource<GroupingPageData>>

    /**
     * Get top podcasts data by genre
     */
    fun getTopPodcastsData(clientScope: CoroutineScope,
                           pageSize: Int,
                           genreId: Int): FlowPaginationModel<Podcast>

    /**
     * Get all podcasts from Lookup query
     */
    suspend fun lookupAsync(ids: List<Long>): List<Podcast>

    /**
     * Get a podcasts from Lookup query
     */
    suspend fun lookupAsync(id: Long): Podcast?

    /**
     * Get room store data URL (room/collection)
     */
    suspend fun getRoomDataAsync(storeFront: String, url: String): RoomPageData

    /**
     * Get multiroom store data by URL (feature/multiroom)
     */
    suspend fun getMultiRoomDataAsync(storeFront: String, url: String): MultiRoomPageData

    /**
     * Get room store data URL (room/collection)
     */
    suspend fun getArtistPageDataAsync(storeFront: String, artistId: Long): ArtistPageData

    /**
     * Get top podcasts Ids from a category
     */
    suspend fun topAsync(category: Int): List<Long>

    /**
     * Get top podcasts of a category
     */
    suspend fun topPodcastsAsync(category: Int): List<Podcast>
}