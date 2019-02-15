package com.caldeirasoft.castly.domain.repository;

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.caldeirasoft.castly.domain.model.Episode

import com.caldeirasoft.castly.domain.model.PodcastWithCount
import com.caldeirasoft.castly.domain.model.SectionWithCount
import kotlinx.coroutines.Deferred

/**
 * Created by Edmond on 15/02/2018.
 */

interface EpisodeRepository {

    /**
     * Select all episodes by podcast id
     */
    fun fetch(feedUrl: String): LiveData<List<Episode>>

    /**
     * Select all episodes by podcast id
     */
    fun fetchFactory(feedUrl: String): DataSource.Factory<Int, Episode>

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
    fun fetch(section: Int, feedUrl: String): LiveData<List<Episode>>

    /**
     * Select all episodes by section
     */
    fun fetchSync(section: Int, feedUrl: String): List<Episode>

    /**
     * Select all episodes by section
     */
    fun fetchFactory(section: Int, feedUrl: String): DataSource.Factory<Int, Episode>

    /**
     * Select inbox episodes count by podcasts
     */
    fun fetchEpisodesCountByPodcast(section: Int): LiveData<List<PodcastWithCount>>

    /**
     * fetch episodes from by section
     */
    fun fetchEpisodeCountBySection(feedUrl: String): LiveData<SectionWithCount>

    /**
     * Select an episode by url
     */
    fun getSync(feedUrl: String, mediaUrl:String): Episode?

    /**
     * Select an episode by id
     */
    fun getSync(episodeId:String): Episode?

    /**
     * Select an episode by id
     */
    fun get(episodeId:String): LiveData<Episode>

    /**
     * Insert an episode in the database. If the podcast already exists, replace it
     */
    fun insert(episode: Episode): Deferred<Unit>

    /**
     * Update an episode
     */
    fun update(episode: Episode): Deferred<Unit>

    /**
     * Update a list of episodes
     */
    fun update(episodes: List<Episode>): Deferred<Unit>

    /**
     * Delete an episode
     */
    fun delete(episode: Episode): Deferred<Unit>

    /**
     * Delete episodes of a podcast by its feedId
     */
    fun deleteByPodcast(feedUrl: String): Deferred<Unit>

    /**
     * Insert episode if not exists
     */
    fun upsert(episode: Episode): Deferred<Unit>
}