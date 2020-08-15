package com.caldeirasoft.castly.domain.repository;

import com.caldeirasoft.castly.domain.model.entities.Episode
import com.caldeirasoft.castly.domain.model.entities.PodcastWithCount
import com.caldeirasoft.castly.domain.model.entities.SectionWithCount
import kotlinx.coroutines.flow.Flow

/**
 * Created by Edmond on 15/02/2018.
 */

interface EpisodeRepository {

    /**
     * Select all episodes by podcast id
     */
    fun fetch(podcastId: Long): Flow<List<Episode>>

    /**
     * Select all episodes by podcast id
     */
    fun fetchSync(podcastId: Long): List<Episode>

    /**
     * Select all episodes by podcast id (with limit and offset)
     */
    fun fetchSync(podcastId: Long, limit: Int, offset: Int): List<Episode>

    /**
     * Select all episodes by section
     */
    fun fetchQueue(): Flow<List<Episode>>

    /**
     * Select all episodes by section
     */
    fun fetchQueueSync(): List<Episode>

    /**
     * Select all episodes by section
     */
    fun fetch(section: Int): Flow<List<Episode>>

    /**
     * Select all episodes by section
     */
    fun fetchSync(section: Int): List<Episode>

    /**
     * Select all episodes by section
     */
    fun fetch(section: Int, podcastId: Long): Flow<List<Episode>>

    /**
     * Select all episodes by section
     */
    fun fetchSync(section: Int, podcastId: Long): List<Episode>

    /**
     * Select inbox episodes count by podcasts
     */
    fun fetchEpisodesCountByPodcast(section: Int): Flow<List<PodcastWithCount>>

    /**
     * fetch episodes from by section
     */
    fun fetchEpisodeCountBySection(podcastId: Long): Flow<SectionWithCount>

    /**
     * Select an episode by id
     */
    fun getSync(episodeId:Long): Episode?

    /**
     * Select an episode by id
     */
    fun get(episodeId: Long): Flow<Episode>

    /**
     * Update an episode
     */
    fun update(episode: Episode)
}