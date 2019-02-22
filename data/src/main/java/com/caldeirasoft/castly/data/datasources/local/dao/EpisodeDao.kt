package com.caldeirasoft.castly.data.datasources.local.dao;

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.caldeirasoft.castly.domain.model.*

/**
 * Created by Edmond on 15/02/2018.
 */

@Dao
interface EpisodeDao {

    /**
     * Select all episodes by podcast id
     */
    @Query("SELECT * FROM Episodes Where feedUrl= :feedUrl ORDER BY published DESC")
    fun fetch(feedUrl: String): LiveData<List<EpisodeEntity>>

    /**
     * Select all episodes by podcast id
     */
    @Query("SELECT * FROM Episodes Where feedUrl= :feedUrl ORDER BY published DESC")
    fun fetchSync(feedUrl: String): List<EpisodeEntity>

    /**
     * Select all episodes by podcast id
     */
    @Query("SELECT * FROM Episodes Where feedUrl = :feedUrl ORDER BY published DESC")
    fun fetchFactory(feedUrl: String): DataSource.Factory<Int, EpisodeEntity>

    /**
     * Select all episodes by section
     */
    @Query("SELECT * FROM Episodes Where section = :section ORDER BY published DESC")
    fun fetch(section: Int): LiveData<List<EpisodeEntity>>

    /**
     * Select all episodes by section
     */
    @Query("SELECT * FROM Episodes Where section = :section ORDER BY published DESC")
    fun fetchSync(section: Int): List<EpisodeEntity>

    /**
     * Select all episodes by section
     * section 0 : all
     * section 1 : queue
     * section 2 : inbox
     * section 3 : archive
     * section 4 : favorite
     * section 5 : history
     */
    @Query("SELECT * FROM Episodes Where CASE :section WHEN 4 THEN isFavorite = 1 WHEN 5 THEN timePlayed != null WHEN 0 THEN 1 = 1 ELSE section = :section END ORDER BY published DESC")
    fun fetchFactory(section: Int): DataSource.Factory<Int, EpisodeEntity>

    /**
     * Select all episodes by section and podcast
     */
    @Query("SELECT * FROM Episodes Where section = :section AND feedUrl = :feedUrl ORDER BY published DESC")
    fun fetch(section: Int, feedUrl: String): LiveData<List<EpisodeEntity>>

    /**
     * Select all episodes by section and podcast
     */
    @Query("SELECT * FROM Episodes Where section = :section AND feedUrl = :feedUrl ORDER BY published DESC")
    fun fetchSync(section: Int, feedUrl: String): List<EpisodeEntity>

    /**
     * Select all episodes by section
     */
    @Query("SELECT * FROM Episodes Where CASE :section WHEN 4 THEN isFavorite = 1 WHEN 5 THEN timePlayed != null WHEN 0 THEN 1 = 1 ELSE section = :section END AND feedUrl = :feedUrl ORDER BY published DESC")
    fun fetchFactory(section: Int, feedUrl: String): DataSource.Factory<Int, EpisodeEntity>

    /**
     * Select all episodes from queue
     */
    @Query("SELECT * FROM Episodes Where section = 1 ORDER BY queuePosition")
    fun fetchQueueSync(): List<EpisodeEntity>

    /**
     * Select all episodes from queue
     */
    @Query("SELECT * FROM Episodes Where section = 1 ORDER BY queuePosition")
    fun fetchQueue(): LiveData<List<EpisodeEntity>>

    /**
     * Select inbox episodes count by podcasts
     */
    @Query("SELECT feedUrl, podcastTitle AS title, imageUrl, COUNT(mediaUrl) AS episodeCount FROM Episodes Where CASE :section WHEN 4 THEN isFavorite = 1 WHEN 5 THEN timePlayed != null WHEN 0 THEN 1 = 1 ELSE section = :section END GROUP BY feedUrl, podcastTitle ORDER BY podcastTitle DESC")
    fun fetchEpisodesCountByPodcast(section: Int): LiveData<List<PodcastWithCountEntity>>

    /**
     * Select inbox episodes count by podcasts
     * section = 1 : queue
     * section = 2 : inbox
     * section = 4 : favorite
     * section = 5 : history
     */
    @Query("SELECT SUM(CASE section WHEN 1 THEN 1 ELSE 0 END) AS QueueCount, SUM(CASE section WHEN 2 THEN 1 ELSE 0 END) AS InboxCount, SUM(CASE isFavorite WHEN 1 THEN 1 ELSE 0 END) AS FavoritesCount, SUM(CASE timePlayed != NULL WHEN 0 THEN 1 ELSE 0 END) AS HistoryCount FROM Episodes Where feedUrl = :feedUrl")
    fun fetchEpisodeCountBySection(feedUrl: String): LiveData<SectionWithCountEntity>

    /**
     * Select an episode by id
     */
    @Query("SELECT * FROM Episodes Where episodeId = :episodeId")
    fun getSync(episodeId: String): EpisodeEntity?

    /**
     * Select an episode by url
     */
    @Query("SELECT * FROM Episodes Where feedUrl = :feedUrl AND mediaUrl = :mediaUrl")
    fun getSync(feedUrl: String, mediaUrl:String): EpisodeEntity?

    /**
     * Select an episode by id
     */
    @Query("SELECT * FROM Episodes Where episodeId = :episodeId")
    fun get(episodeId:String): LiveData<EpisodeEntity>

    /**
     * Select a podcast by id
     */
    @Query("SELECT * FROM podcasts Where feedUrl = :feedUrl")
    fun getPodcastSync(feedUrl: String): PodcastEntity

    /**
     * Insert an episode in the database. If the podcast already exists, replace it
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(episode: EpisodeEntity)

    /**
     * Insert episodes in the database. If the podcast already exists, replace it
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(episodes: List<EpisodeEntity>)

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
    @Query("DELETE FROM Episodes Where feedUrl = :feedUrl")
    fun deleteByPodcast(feedUrl: String)
}
