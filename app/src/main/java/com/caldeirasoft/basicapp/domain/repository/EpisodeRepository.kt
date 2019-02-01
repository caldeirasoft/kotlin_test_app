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
    fun insertEpisode(episode: Episode)

    /**
     * Update a podcast
     */
    fun updatePodcast(podcast: Podcast)

    /**
     * Update an episode
     */
    fun updateEpisode(episode: Episode)

    /**
     * Update a list of episodes
     */
    fun updateEpisodes(episodes: List<Episode>)

    /**
     * Delete an episode
     */
    fun deleteEpisode(episode: Episode)

    /**
     * Delete episodes of a podcast by its feedId
     */
    fun deleteEpisodes(feedUrl: String)

    /**
     * Archive episode
     */
    fun archiveEpisode(episode: Episode)

    /**
     * Toggle episode favorite status
     */
    fun toggleEpisodeFavorite(episode: Episode)

    /**
     * Queue episode first
     */
    fun queueEpisodeFirst(episode: Episode)

    /**
     * Queue episode last
     */
    fun queueEpisodeLast(episode: Episode)

    /**
     * Queue episode last
     */
    fun playEpisode(episode: Episode)

    /**
     * Reorder playlist queue
     */
    fun reorderQueue()

    /**
     * Insert episode if not exists
     */
    fun upsertEpisode(episode: Episode)
}