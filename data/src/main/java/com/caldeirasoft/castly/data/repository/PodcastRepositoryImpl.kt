package com.caldeirasoft.castly.data.repository

import androidx.lifecycle.*
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.data.datasources.local.dao.PodcastDao
import com.caldeirasoft.castly.data.extensions.convert
import com.caldeirasoft.castly.domain.model.PodcastEntity
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

/**
 * Created by Edmond on 15/02/2018.
 */
class PodcastRepositoryImpl(val podcastDao: PodcastDao) : PodcastRepository
{
    private val TAG = PodcastRepository::class.java.simpleName


    /**
     * Select all podcasts from the database
     */
    override fun fetch(): LiveData<List<Podcast>>
            = podcastDao.fetch().convert()


    /**
     * Select all podcasts from the database
     */
    override fun fetchSync(): List<Podcast>
            = podcastDao.fetchSync().map { it as Podcast }

    /**
     * Select a podcast by id
     */
    override fun get(feedUrl:String): LiveData<Podcast>
            = podcastDao.get(feedUrl).convert()

    /**
     * Select a podcast by id
     */
    override fun getSync(feedUrl:String): Podcast?
            = podcastDao.getSync(feedUrl)

    /**
     * Insert a podcast in the database. If the podcast already exists, replace it
     */
    override fun insert(podcast: Podcast): Deferred<Unit> {
        return GlobalScope.async {
            podcast as PodcastEntity
            podcastDao.insert(podcast)
        }
    }

    /**
     * Update a podcast
     */
    override fun update(podcast: Podcast): Deferred<Unit> {
        return GlobalScope.async {
            podcast as PodcastEntity
            podcastDao.update(podcast)
        }
    }

    /**
     * Delete a podcast
     */
    override fun delete(podcast: Podcast): Deferred<Unit> {
        return GlobalScope.async {
            podcast as PodcastEntity
            podcastDao.delete(podcast)
        }
    }
}