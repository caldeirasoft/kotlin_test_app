package com.caldeirasoft.castly.data.datasources.local.dao;


import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.caldeirasoft.castly.domain.model.PodcastEntity

/**
 * Created by Edmond on 15/02/2018.
 */

@Dao
interface PodcastDao {

    /**
     * Select all podcasts from the database
     */
    @Query("SELECT * FROM podcasts")
    fun fetch(): LiveData<List<PodcastEntity>>

    /**
     * Select all podcasts from the database
     */
    @Query("SELECT * FROM podcasts")
    fun fetchSync(): List<PodcastEntity>

    /**
     * Select all podcasts from the database
     */
    @Query("SELECT * FROM podcasts")
    fun fetchFactory(): DataSource.Factory<Int, PodcastEntity>

    /**
     * Select a podcast by id
     */
    @Query("SELECT * FROM podcasts Where feedUrl = :feedUrl")
    fun get(feedUrl: String): LiveData<PodcastEntity>

    /**
     * Select a podcast by id
     */
    @Query("SELECT * FROM podcasts Where feedUrl = :feedUrl")
    fun getSync(feedUrl: String): PodcastEntity?

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