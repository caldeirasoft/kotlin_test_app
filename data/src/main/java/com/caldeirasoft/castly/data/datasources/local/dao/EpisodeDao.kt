package com.caldeirasoft.castly.data.datasources.local.dao;

import androidx.room.*
import com.caldeirasoft.castly.data.entity.EpisodeEntity
import com.caldeirasoft.castly.data.entity.PodcastEntity
import com.caldeirasoft.castly.data.entity.PodcastWithCountEntity
import com.caldeirasoft.castly.data.entity.SectionWithCountEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Edmond on 15/02/2018.
 */

@Dao
interface EpisodeDao {

    /**
     * Select all episodes by podcast id
     */
    @Query("SELECT * FROM Episodes Where podcastId = :podcastId ORDER BY releaseDate DESC")
    fun fetch(podcastId: Long): Flow<List<EpisodeEntity>>

    /**
     * Select all episodes by podcast id
     */
    @Query("SELECT * FROM Episodes Where podcastId = :podcastId ORDER BY releaseDate DESC")
    fun fetchSync(podcastId: Long): List<EpisodeEntity>

    /**
     * Select all episodes by podcast id
     */
    @Query("SELECT * FROM Episodes Where podcastId = :podcastId ORDER BY releaseDate DESC LIMIT :limit OFFSET :offset")
    fun fetchSync(podcastId: Long, limit: Int, offset: Int): List<EpisodeEntity>

    /**
     * Count episodes of a podcast
     */
    @Query("SELECT COUNT(id) FROM episodes WHERE podcastId = :podcastId")
    fun count(podcastId: Long): Int

    /**
     * Select all episodes by section
     * section 0 : all
     * section 1 : queue
     * section 2 : inbox
     * section 3 : archive
     * section 4 : favorite
     * section 5 : history
     */
    @Query("SELECT * FROM Episodes" +
            " Where CASE :section WHEN 4 THEN isFavorite = 1 WHEN 5 THEN timePlayed != null WHEN 0 THEN 1 = 1 ELSE section = :section END" +
            " ORDER BY releaseDate DESC")
    fun fetch(section: Int): Flow<List<EpisodeEntity>>

    /**
     * Select all episodes by section
     */
    @Query("SELECT * FROM Episodes" +
            " Where CASE :section WHEN 4 THEN isFavorite = 1 WHEN 5 THEN timePlayed != null WHEN 0 THEN 1 = 1 ELSE section = :section END" +
            " ORDER BY releaseDate DESC")
    fun fetchSync(section: Int): List<EpisodeEntity>

    /**
     * Select all episodes by section and podcast
     */
    @Query("SELECT * FROM Episodes" +
            " Where CASE :section WHEN 4 THEN isFavorite = 1 WHEN 5 THEN timePlayed != null WHEN 0 THEN 1 = 1 ELSE section = :section END" +
            " AND podcastId = :podcastId ORDER BY releaseDate DESC")
    fun fetchSync(section: Int, podcastId: Long): List<EpisodeEntity>

    /**
     * Select all episodes by section and podcast
     */
    @Query("SELECT * FROM Episodes" +
            " Where CASE :section WHEN 4 THEN isFavorite = 1 WHEN 5 THEN timePlayed != null WHEN 0 THEN 1 = 1 ELSE section = :section END" +
            " AND podcastId = :podcastId ORDER BY releaseDate DESC")
    fun fetch(section: Int, podcastId: Long): Flow<List<EpisodeEntity>>

    /**
     * Select all episodes from queue
     */
    @Query("SELECT * FROM Episodes Where section = 1 ORDER BY queuePosition")
    fun fetchQueueSync(): List<EpisodeEntity>

    /**
     * Select all episodes from queue
     */
    @Query("SELECT * FROM Episodes Where section = 1 ORDER BY queuePosition")
    fun fetchQueue(): Flow<List<EpisodeEntity>>

    /**
     * Select inbox episodes count by podcasts
     */
    @Query("SELECT podcastId AS id, podcastName AS title, artwork, COUNT(id) AS episodeCount FROM Episodes Where CASE :section WHEN 4 THEN isFavorite = 1 WHEN 5 THEN timePlayed != null WHEN 0 THEN 1 = 1 ELSE section = :section END GROUP BY podcastId, podcastName ORDER BY podcastName DESC")
    fun fetchEpisodesCountByPodcast(section: Int): Flow<List<PodcastWithCountEntity>>

    /**
     * Select inbox episodes count by podcasts
     * section = 1 : queue
     * section = 2 : inbox
     * section = 4 : favorite
     * section = 5 : history
     */
    @Query("SELECT SUM(CASE section WHEN 1 THEN 1 ELSE 0 END) AS QueueCount, SUM(CASE section WHEN 2 THEN 1 ELSE 0 END) AS InboxCount, SUM(CASE isFavorite WHEN 1 THEN 1 ELSE 0 END) AS FavoritesCount, SUM(CASE timePlayed != NULL WHEN 0 THEN 1 ELSE 0 END) AS HistoryCount FROM Episodes Where podcastId = :podcastId")
    fun fetchEpisodeCountBySection(podcastId: Long): Flow<SectionWithCountEntity>

    /**
     * Select an episode by id
     */
    @Query("SELECT * FROM Episodes Where id = :episodeId")
    fun getSync(episodeId: Long): EpisodeEntity?

    /**
     * Select an episode by id
     */
    @Query("SELECT * FROM Episodes Where id = :episodeId")
    fun get(episodeId: Long): Flow<EpisodeEntity>

    /**
     * Select a podcast by id
     */
    @Query("SELECT * FROM podcasts Where id = :podcastId")
    fun getPodcastSync(podcastId: Long): PodcastEntity

    /**
     * Insert an episode in the database. If the podcast already exists, replace it
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(episode: EpisodeEntity)

    /**
     * Insert episodes in the database. If the podcast already exists, ignore
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgore(episodes: List<EpisodeEntity>)

    /**
     * Update a podcast
     */
    @Update
    fun update(podcast: PodcastEntity)

    /**
     * Update an episode
     */
    @Update
    fun update(episode: EpisodeEntity)

    /**
     * Update an episode
     */
    @Update
    fun update(episodes: List<EpisodeEntity>)

    /**
     * Delete an episode
     */
    @Delete
    fun delete(episode: EpisodeEntity)

    /**
     * Delete episodes of a podcast by its feedId
     */
    @Query("DELETE FROM Episodes Where podcastId = :podcastId")
    fun deleteByPodcast(podcastId: Long)
}
