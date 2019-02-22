package com.caldeirasoft.castly.domain.repository;

import androidx.lifecycle.LiveData
import com.caldeirasoft.castly.domain.model.Podcast;
import kotlinx.coroutines.Deferred

/**
 * Created by Edmond on 15/02/2018.
 */

interface PodcastRepository {

    /**
     * Select all podcasts from the database
     */
    fun fetch(): LiveData<List<Podcast>>

    /**
     * Select all podcasts from the database
     */
    fun fetchSync(): List<Podcast>

    /**
     * Select a podcast by id
     */
    fun get(feedUrl: String): LiveData<Podcast>

    /**
     * Select a podcast by id
     */
    fun getSync(feedUrl: String): Podcast?

    /**
     * Insert a podcast in the database. If the podcast already exists, replace it
     */
    fun insert(podcast: Podcast)

    /**
     * Update a podcast
     */
    fun update(podcast: Podcast)

    /**
     * Delete a podcast
     */
    fun delete(podcast: Podcast)
}