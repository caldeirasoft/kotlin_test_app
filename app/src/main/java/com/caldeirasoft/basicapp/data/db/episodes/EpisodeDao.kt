package com.caldeirasoft.basicapp.data.db.episodes;

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.caldeirasoft.basicapp.data.entity.Episode

import com.caldeirasoft.basicapp.data.entity.Podcast;
import com.caldeirasoft.basicapp.data.entity.PodcastWithCount
import com.caldeirasoft.basicapp.data.entity.SectionWithCount

/**
 * Created by Edmond on 15/02/2018.
 */

@Dao
interface EpisodeDao {

    /**
     * Select all episodes by podcast id
     */
    @Query("SELECT * FROM Episodes Where feedUrl = :feedUrl ORDER BY published DESC")
    fun fetchEpisodes(feedUrl: String): DataSource.Factory<Int, Episode>

    /**
     * Select all episodes by podcast id
     */
    @Query("SELECT * FROM Episodes Where feedUrl= :feedUrl ORDER BY published DESC")
    fun getEpisodes(feedUrl: String): LiveData<List<Episode>>

    /**
     * Select all episodes by section
     */
    @Query("SELECT * FROM Episodes Where section = :section ORDER BY published DESC")
    fun getEpisodesBySection(section: Int): LiveData<List<Episode>>

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
    fun fetchEpisodesBySection(section: Int): DataSource.Factory<Int, Episode>

    /**
     * Select all episodes by section
     */
    @Query("SELECT * FROM Episodes Where section = :section AND feedUrl = :feedUrl ORDER BY published DESC")
    fun getEpisodesBySection(section: Int, feedUrl: String): LiveData<List<Episode>>

    /**
     * Select all episodes by section
     */
    @Query("SELECT * FROM Episodes Where CASE :section WHEN 4 THEN isFavorite = 1 WHEN 5 THEN timePlayed != null WHEN 0 THEN 1 = 1 ELSE section = :section END AND feedUrl = :feedUrl ORDER BY published DESC")
    fun fetchEpisodesBySection(section: Int, feedUrl: String): DataSource.Factory<Int, Episode>

    /**
     * Select inbox episodes count by podcasts
     */
    @Query("SELECT feedUrl, podcastTitle AS title, imageUrl, COUNT(mediaUrl) AS episodeCount FROM Episodes Where CASE :section WHEN 4 THEN isFavorite = 1 WHEN 5 THEN timePlayed != null WHEN 0 THEN 1 = 1 ELSE section = :section END GROUP BY feedUrl, podcastTitle ORDER BY podcastTitle DESC")
    fun fetchEpisodesCoutByPodcast(section: Int): LiveData<List<PodcastWithCount>>

    /**
     * Select inbox episodes count by podcasts
     * section = 1 : queue
     * section = 2 : inbox
     * section = 4 : favorite
     * section = 5 : history
     */
    @Query("SELECT SUM(CASE section WHEN 1 THEN 1 ELSE 0 END) AS QueueCount, SUM(CASE section WHEN 2 THEN 1 ELSE 0 END) AS InboxCount, SUM(CASE isFavorite WHEN 1 THEN 1 ELSE 0 END) AS FavoritesCount, SUM(CASE timePlayed != NULL WHEN 0 THEN 1 ELSE 0 END) AS HistoryCount FROM Episodes Where feedUrl = :feedUrl")
    fun fetchEpisodeCountBySection(feedUrl: String): LiveData<SectionWithCount>

    /**
     * Select an episode by id
     */
    @Query("SELECT * FROM Episodes Where episodeId = :episodeId")
    fun getEpisodeById(episodeId: String): Episode?

    /**
     * Select an episode by url
     */
    @Query("SELECT * FROM Episodes Where feedUrl = :feedUrl AND mediaUrl = :mediaUrl")
    fun getEpisodeByUrl(feedUrl: String, mediaUrl:String): Episode?

    /**
     * Select an episode by id
     */
    @Query("SELECT * FROM Podcasts Where feedUrl = :feedUrl")
    fun getPodcastById(feedUrl: String): Podcast?

    /**
     * Insert an episode in the database. If the podcast already exists, replace it
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEpisode(episode: Episode)

    /**
     * Insert episodes in the database. If the podcast already exists, replace it
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEpisodes(episodes: List<Episode>)

    /**
     * Update a podcast
     */
    @Update
    fun updatePodcast(podcast: Podcast)

    /**
     * Update an episode
     */
    @Update fun updateEpisode(episode: Episode)

    /**
     * Update an episode
     */
    @Update fun updateEpisodes(episodes: List<Episode>)

    /**
     * Delete an episode
     */
    @Delete fun deleteEpisode(episode: Episode)

    /**
     * Delete episodes of a podcast by its feedId
     */
    @Query("DELETE FROM Episodes Where feedUrl = :feedUrl")
    fun deleteEpisodes(feedUrl: String)
}
