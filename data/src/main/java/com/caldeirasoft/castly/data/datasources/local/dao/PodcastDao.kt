package com.caldeirasoft.castly.data.datasources.local.dao;


import androidx.room.*
import com.caldeirasoft.castly.data.entity.PodcastEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Edmond on 15/02/2018.
 */

@Dao
interface PodcastDao {

    /**
     * Select all podcasts from the database
     */
    @Query("SELECT * FROM podcasts")
    fun fetch(): Flow<List<PodcastEntity>>

    /**
     * Select all subscribed podcasts from the database
     */
    @Query("SELECT * FROM podcasts")
    fun fetchSubscribed(): Flow<List<PodcastEntity>>

    /**
     * Select all podcasts from the database
     */
    @Query("SELECT * FROM podcasts")
    fun fetchSync(): List<PodcastEntity>

    /**
     * Select a podcast by id
     */
    @Query("SELECT * FROM podcasts Where id = :podcastId")
    fun get(podcastId: Long): Flow<PodcastEntity>

    /**
     * Select a podcast by id
     */
    @Query("SELECT * FROM podcasts Where id = :podcastId")
    fun getSync(podcastId: Long): PodcastEntity?

    /**
     * Insert a podcast in the database. If the podcast already exists, replace it
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(podcast: PodcastEntity)

    /**
     * Update a podcast
     */
    @Update
    fun update(podcast: PodcastEntity)

    /**
     * Delete a podcast
     */
    @Delete
    fun delete(podcast: PodcastEntity)
}
