package com.caldeirasoft.basicapp.data.repository

import androidx.lifecycle.*
import com.caldeirasoft.basicapp.domain.repository.PodcastRepository
import com.caldeirasoft.basicapp.domain.entity.Podcast
import com.caldeirasoft.basicapp.data.datasources.local.dao.PodcastDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    override suspend fun insertPodcast(podcast: Podcast)
    {
        withContext(Dispatchers.IO){
            podcastDao.insertPodcast(podcast)
        }
    }

    /**
     * Update a podcast
     */
    override suspend fun updatePodcast(podcast: Podcast) {
        withContext(Dispatchers.IO) {
            podcastDao.updatePodcast(podcast)
        }
    }

    /**
     * Delete a podcast
     */
    override suspend fun deletePodcast(podcast: Podcast) {
        withContext(Dispatchers.IO) {
            podcastDao.deletePodcast(podcast)
        }
    }
}