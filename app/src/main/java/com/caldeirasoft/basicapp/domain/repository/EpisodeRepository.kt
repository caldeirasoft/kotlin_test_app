package com.caldeirasoft.basicapp.domain.repository;

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.caldeirasoft.basicapp.domain.entity.Episode

import com.caldeirasoft.basicapp.domain.entity.Podcast;
import com.caldeirasoft.basicapp.domain.entity.PodcastWithCount
import com.caldeirasoft.basicapp.domain.entity.SectionWithCount
import com.caldeirasoft.basicapp.presentation.datasource.EpisodeDbDataSourceFactory

/**
 * Created by Edmond on 15/02/2018.
 */

interface EpisodeRepository {

    /**
     * Select all episodes by podcast id
     */
    fun getEpisodes(feedUrl: String): LiveData<List<Episode>>

    /**
     * Select all episodes by podcast id
     */
    fun fetchEpisodes(feedUrl: String): DataSource.Factory<Int, Episode>

    /**
     * Select all episodes by section
     */
    fun getEpisodesBySection(section: Int): LiveData<List<Episode>>

    /**
     * Select all episodes by section
     */
    fun fetchEpisodesBySection(section: Int): DataSource.Factory<Int, Episode>

    /**
     * Select all episodes by section
     */
    fun getEpisodesBySection(section: Int, feedUrl: String): LiveData<List<Episode>>

    /**
     * Select all episodes by section
     */
    fun fetchEpisodesBySection(section: Int, feedUrl: String): DataSource.Factory<Int, Episode>

    /**
     * Select inbox episodes count by podcasts
     */
    fun fetchEpisodesCoutByPodcast(section: Int): LiveData<List<PodcastWithCount>>

    /**
     * fetch episodes from by section
     */
    fun fetchEpisodeCountBySection(feedUrl: String): LiveData<SectionWithCount>

    /**
     * Select an episode by url
     */
    fun getEpisodeByUrl(feedUrl: String, mediaUrl:String): Episode?

    /**
     * Select an episode by id
     */
    fun getEpisodeById(episodeId:String): Episode?

    /**
     * Select an episode by id
     */
    fun getEpisodeLive(episodeId:String): LiveData<Episode>

    /**
     * Insert an episode in the database. If the podcast already exists, replace it
     */
    suspend fun insertEpisode(episode: Episode)

    /**
     * Update a podcast
     */
    suspend fun updatePodcast(podcast: Podcast)

    /**
     * Update an episode
     */
    suspend fun updateEpisode(episode: Episode)

    /**
     * Update a list of episodes
     */
    suspend fun updateEpisodes(episodes: List<Episode>)

    /**
     * Delete an episode
     */
    suspend fun deleteEpisode(episode: Episode)

    /**
     * Delete episodes of a podcast by its feedId
     */
    suspend fun deleteEpisodes(feedUrl: String)

    /**
     * Archive episode
     */
    suspend fun archiveEpisode(episode: Episode)

    /**
     * Toggle episode favorite status
     */
    suspend fun toggleEpisodeFavorite(episode: Episode)

    /**
     * Queue episode first
     */
    suspend fun queueEpisodeFirst(episode: Episode)

    /**
     * Queue episode last
     */
    suspend fun queueEpisodeLast(episode: Episode)

    /**
     * Queue episode last
     */
    suspend fun playEpisode(episode: Episode)

    /**
     * Reorder playlist queue
     */
    suspend fun reorderQueue()

    /**
     * Insert episode if not exists
     */
    suspend fun upsertEpisode(episode: Episode)
}