package com.caldeirasoft.castly.domain.repository;

import com.caldeirasoft.castly.domain.model.entities.Podcast;
import com.caldeirasoft.castly.domain.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Created by Edmond on 15/02/2018.
 */

interface PodcastRepository {

    /**
     * Get the cache subscribed podcasts from DB
     */
    fun fetchSubscribedPodcasts(): Flow<List<Podcast>>

    /**
     * Gets the cached podcast from database and tries to get
     * fresh podcast from web and save into database
     * if that fails then continues showing cached data.
     */
    fun getPodcast(podcastId: Long): Flow<Resource<Podcast>>

    /**
     * Gets the cached podcast from database and tries to get
     * fresh podcast from web and save into database
     * if that fails then continues showing cached data.
     */
    fun getPodcast(podcast: Podcast): Flow<Resource<Podcast>>

    /**
     * Select all podcasts from the database
     */
    fun fetchSync(): List<Podcast>
}