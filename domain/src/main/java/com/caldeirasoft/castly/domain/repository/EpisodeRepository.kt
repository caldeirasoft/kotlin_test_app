package com.caldeirasoft.castly.domain.repository;

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.PodcastWithCount
import com.caldeirasoft.castly.domain.model.SectionWithCount

/**
 * Created by Edmond on 15/02/2018.
 */

interface EpisodeRepository {

    /**
     * Select all episodes by podcast id
     */
    fun fetch(podcastId: Long): LiveData<List<Episode>>

    /**
     * Select all episodes by podcast id
     */
    fun fetchSync(podcastId: Long): List<Episode>

    /**
     * Select all episodes by podcast id (with limit and offset)
     */
    fun fetchSync(podcastId: Long, limit: Int, offset: Int): List<Episode>

    /**
     * Select all episodes by podcast id
     */
    fun fetchFactory(podcastId: Long): DataSource.Factory<Int, Episode>

    /**
     * Count all episodes by podcast id
     */
    fun count(podcastId: Long): Int

    /**
     * Select all episodes by section
     */
    fun fetchQueue(): LiveData<List<Episode>>

    /**
     * Select all episodes by section
     */
    fun fetchQueueSync(): List<Episode>

    /**
     * Select all episodes by section
     */
    fun fetch(section: Int): LiveData<List<Episode>>

    /**
     * Select all episodes by section
     */
    fun fetchSync(section: Int): List<Episode>

    /**
     * Select all episodes by section
     */
    fun fetchFactory(section: Int): DataSource.Factory<Int, Episode>

    /**
     * Select all episodes by section
     */
    fun fetch(section: Int, podcastId: Long): LiveData<List<Episode>>

    /**
     * Select all episodes by section
     */
    fun fetchSync(section: Int, podcastId: Long): List<Episode>

    /**
     * Select all episodes by section
     */
    fun fetchFactory(section: Int, podcastId: Long): DataSource.Factory<Int, Episode>

    /**
     * Select inbox episodes count by podcasts
     */
    fun fetchEpisodesCountByPodcast(section: Int): LiveData<List<PodcastWithCount>>

    /**
     * fetch episodes from by section
     */
    fun fetchEpisodeCountBySection(podcastId: Long): LiveData<SectionWithCount>

    /**
     * Select an episode by id
     */
    fun getSync(episodeId:Long): Episode?

    /**
     * Select an episode by id
     */
    fun get(episodeId:Long): LiveData<Episode>

    /**
     * Insert an episode in the database. If the podcast already exists, replace it
     */
    fun insert(episode: Episode)

    /**
     * Insert a list of episodes
     */
    fun insertIgore(episodes: List<Episode>)

    /**
     * Update an episode
     */
    fun update(episode: Episode)

    /**
     * Update a list of episodes
     */
    fun update(episodes: List<Episode>)

    /**
     * Delete an episode
     */
    fun delete(episode: Episode)

    /**
     * Delete episodes of a podcast by its feedId
     */
    fun deleteByPodcast(podcastId: Long)

    /**
     * Insert episode if not exists
     */
    fun upsert(episode: Episode)
}