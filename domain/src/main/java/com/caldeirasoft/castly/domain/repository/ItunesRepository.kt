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
    fun lookup(ids: List<Int>): Deferred<List<Podcast>>

    /**
     * Get store front
     */
    fun getStore(storeFront: String): Deferred<ItunesStore>

    /**
     * Get top podcasts Ids from a category
     */
    fun top(category: Int): Deferred<List<Int>>
}