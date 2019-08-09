package com.caldeirasoft.castly.data.repository

import androidx.lifecycle.LiveData
import com.caldeirasoft.castly.data.datasources.local.dao.PodcastDao
import com.caldeirasoft.castly.data.extensions.convert
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.model.PodcastEntity
import com.caldeirasoft.castly.domain.repository.PodcastRepository

/**
 * Created by Edmond on 15/02/2018.
 */
class PodcastRepositoryImpl(val podcastDao: PodcastDao) : PodcastRepository {
    private val TAG = PodcastRepository::class.java.simpleName


    /**
     * Select all podcasts from the database
     */
    override fun fetch(): LiveData<List<Podcast>> = podcastDao.fetch().convert()


    /**
     * Select all podcasts from the database
     */
    override fun fetchSync(): List<Podcast> = podcastDao.fetchSync()

    /**
     * Select a podcast by id
     */
    override fun get(podcastId: Long): LiveData<Podcast> = podcastDao.get(podcastId).convert()

    /**
     * Select a podcast by id
     */
    override fun getSync(podcastId: Long): Podcast? = podcastDao.getSync(podcastId)

    /**
     * Insert a podcast in the database. If the podcast already exists, replace it
     */
    override fun insert(podcast: Podcast) {
        podcast as PodcastEntity
        podcastDao.insert(podcast)
    }

    /**
     * Update a podcast
     */
    override fun update(podcast: Podcast) {
        podcast as PodcastEntity
        podcastDao.update(podcast)
    }

    /**
     * Delete a podcast
     */
    override fun delete(podcast: Podcast) {
        podcast as PodcastEntity
        podcastDao.delete(podcast)
    }
}