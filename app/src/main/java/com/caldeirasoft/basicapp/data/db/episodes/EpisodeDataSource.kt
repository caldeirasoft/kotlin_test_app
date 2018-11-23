package com.caldeirasoft.basicapp.data.db.episodes;

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.caldeirasoft.basicapp.data.entity.Episode

import com.caldeirasoft.basicapp.data.entity.Podcast;
import com.caldeirasoft.basicapp.data.entity.PodcastWithCount
import com.caldeirasoft.basicapp.data.entity.SectionWithCount
import com.caldeirasoft.basicapp.data.repository.EpisodeDbDataSourceFactory
import com.caldeirasoft.basicapp.data.repository.EpisodeFeedlyDataSourceFactory

/**
 * Created by Edmond on 15/02/2018.
 */

interface EpisodeDataSource {

    interface LoadEpisodesCallback
    {
        fun onEpisodesLoaded(episodes: List<Episode>, continuation: String?)
        fun onDataNotAvailable()
    }

    interface GetEpisodeCallback
    {
        fun onEpisodeLoaded(episode: Episode)
        fun onDataNotAvailable()
    }

    /**
     * Select a podcast by id
     */
    fun getPodcastById(feedUrl: String): LiveData<Podcast>

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
     * fetch episodes from Feedly
     */
    fun getEpisodeDataSourceFromFeedly(feed: Podcast): EpisodeFeedlyDataSourceFactory

    /**
     * fetch inbox episodes data source factory
     */
    fun getEpisodeDbDataSource(section:Int, feedUrl: String?): EpisodeDbDataSourceFactory

    /**
     * Select an episode by url
     */
    fun getEpisodeByUrl(feedUrl: String, mediaUrl:String): Episode?

    /**
     * Select an episode by id
     */
    fun getEpisodeById(episodeId:String): Episode?

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