package com.caldeirasoft.castly.domain.repository;

import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.model.itunes.StoreCollection
import com.caldeirasoft.castly.domain.model.itunes.StoreData
import com.caldeirasoft.castly.domain.model.itunes.StoreMultiCollection

/**
 * Created by Edmond on 15/02/2018.
 */

interface ItunesRepository {

    /**
     * Get all podcasts from Lookup query
     */
    suspend fun lookupAsync(ids: List<Long>): List<Podcast>

    /**
     * Get a podcasts from Lookup query
     */
    suspend fun lookupAsync(id: Long): Podcast?

    /**
     * Get store front
     */
    suspend fun getStoreAsync(storeFront: String): StoreData

    /**
     * Get store data by genre
     */
    suspend fun getGenreDataAsync(storeFront: String, genreId: Int): StoreData

    /**
     * Get store data for a collection
     */
    suspend fun getCollectionDataAsync(storeFront: String, id: Int): StoreCollection

    /**
     * Get store data for a collection
     */
    suspend fun getMultiRoomDataAsync(storeFront: String, id: Int): StoreMultiCollection

    /**
     * Get store data for a collection
     */
    suspend fun getFeatureDataAsync(storeFront: String, id: Int): StoreMultiCollection

    /**
     * Get top podcasts Ids from a category
     */
    suspend fun topAsync(category: Int): List<Long>

    /**
     * Get top podcasts of a category
     */
    suspend fun topPodcastsAsync(category: Int): List<Podcast>

    /**
     * Get podcast info
     */
    suspend fun getPodcastAsync(storeFront: String, id: Long): Podcast?
}