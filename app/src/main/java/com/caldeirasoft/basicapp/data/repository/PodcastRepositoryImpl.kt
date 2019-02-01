package com.caldeirasoft.basicapp.data.repository

import androidx.lifecycle.*
import com.caldeirasoft.basicapp.domain.repository.PodcastRepository
import com.caldeirasoft.basicapp.domain.entity.Podcast
import com.caldeirasoft.basicapp.data.datasources.local.dao.PodcastDao
import org.jetbrains.anko.doAsync

/**
 * Created by Edmond on 15/02/2018.
 */
class PodcastRepositoryImpl(val podcastDao: PodcastDao) : PodcastRepository
{
    private val TAG = PodcastRepository::class.java.simpleName


    override fun getPodcastsFromDb(): LiveData<List<Podcast>> =
        podcastDao.getPodcasts()


    override fun getPodcastById(feedUrl:String): LiveData<Podcast> = podcastDao.getPodcastById(feedUrl)

    /**
     * Insert a podcast in the database. If the podcast already exists, replace it
     */
    override fun insertPodcast(podcast: Podcast)
    {
        doAsync {
            podcastDao.insertPodcast(podcast)
        }
    }

    /**
     * Update a podcast
     */
    override fun updatePodcast(podcast: Podcast) {
        doAsync {
            podcastDao.updatePodcast(podcast)
        }
    }

    /**
     * Delete a podcast
     */
    override fun deletePodcast(podcast: Podcast) {
        doAsync {
            podcastDao.deletePodcast(podcast)
        }
    }
}