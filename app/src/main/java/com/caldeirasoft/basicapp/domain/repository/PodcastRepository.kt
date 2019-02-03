package com.caldeirasoft.basicapp.domain.repository;

import androidx.lifecycle.LiveData
import com.caldeirasoft.basicapp.domain.entity.Podcast;

/**
 * Created by Edmond on 15/02/2018.
 */

interface PodcastRepository {

    /**
     * Select all podcasts from the database
     */
    fun getPodcastsFromDb(): LiveData<List<Podcast>>

    /**
     * Select a podcast by id
     */
    fun getPodcastById(feedUrl: String): LiveData<Podcast>

    /**
     * Insert a podcast in the database. If the podcast already exists, replace it
     */
    suspend fun insertPodcast(podcast: Podcast)

    /**
     * Update a podcast
     */
    suspend fun updatePodcast(podcast: Podcast)

    /**
     * Delete a podcast
     */
    suspend fun deletePodcast(podcast: Podcast)
}