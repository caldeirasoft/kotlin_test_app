package com.caldeirasoft.basicapp.data.datasources.local.dao;


import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.caldeirasoft.basicapp.domain.entity.Podcast;

/**
 * Created by Edmond on 15/02/2018.
 */

@Dao interface PodcastDao {

    /**
     * Select all podcasts from the database
     */
    @Query("SELECT * FROM podcasts") fun getPodcasts(): LiveData<List<Podcast>>

    /**
     * Select all podcasts from the database
     */
    @Query("SELECT * FROM podcasts") fun getPodcastDataSource(): DataSource.Factory<Int, Podcast>



    /**
     * Select a podcast by id
     */
    @Query("SELECT * FROM podcasts Where feedUrl = :feedUrl") fun getPodcastById(feedUrl: String): LiveData<Podcast>

    /**
     * Insert a podcast in the database. If the podcast already exists, replace it
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertPodcast(podcast: Podcast)

    /**
     * Update a podcast
     */
    @Update
    fun updatePodcast(podcast: Podcast)

    /**
     * Delete a podcast
     */
    @Delete
    fun deletePodcast(podcast: Podcast)
}
