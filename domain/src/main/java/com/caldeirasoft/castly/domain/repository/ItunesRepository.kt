package com.caldeirasoft.castly.domain.repository;

import com.caldeirasoft.castly.domain.model.itunes.ItunesStore
import com.caldeirasoft.castly.domain.model.Podcast;
import kotlinx.coroutines.Deferred

/**
 * Created by Edmond on 15/02/2018.
 */

interface ItunesRepository {

    /**
     * Get all podcasts from Lookup query
     */
    suspend fun lookup(ids: List<Int>): List<Podcast>

    /**
     * Get store front
     */
    suspend fun getStore(storeFront: String): ItunesStore

    /**
     * Get top podcasts Ids from a category
     */
    suspend fun top(category: Int): List<Int>
}