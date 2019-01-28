package com.caldeirasoft.basicapp.data.db.podcasts;

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast;
import com.caldeirasoft.basicapp.data.repository.ItunesLookupDataSourceFactory
import com.caldeirasoft.basicapp.data.repository.ItunesStoreSourceFactory
import com.caldeirasoft.basicapp.data.repository.PodcastItunesDataSourceFactory
import kotlinx.coroutines.Deferred

/**
 * Created by Edmond on 15/02/2018.
 */

interface PodcastDataSource {

    /**
     * Select all podcasts from the database
     */
    fun getPodcastsFromDb(): LiveData<List<Podcast>>

    /**
     * Select a podcast by id
     */
    fun getPodcastById(feedUrl: String): LiveData<Podcast>

    /**
     * Get podcast from Feedly ID
     */
    fun getPodcastFromFeedlyApi(feedUrl: String): Deferred<Podcast?>

    /**
     * Select all podcasts from catalog
     */
    fun getPodcastsFromItunesCatalog(pageSize: Int): LiveData<PagedList<Podcast>>

    /**
     * Select all podcasts from catalog
     */
    fun getPodcastsDataSourceFromDb(): DataSource.Factory<Int, Podcast>

    /**
     * Get all podcasts data source from catalog
     */
    fun getPodcastsDataSourceFactoryFromItunes(genre: Int): PodcastItunesDataSourceFactory

    /**
     * Get podcasts from itunes store front
     */
    fun getItunesStoreSourceFactory(storeFront: String): ItunesStoreSourceFactory

    /**
     * Get podcasts from itunes lookup ids
     */
    fun getItunesLookupDataSourceFactory(ids: List<Int>): ItunesLookupDataSourceFactory

    /**
     * Get last episode from podcast
     */
    fun getLastEpisode(podcast: Podcast): Deferred<Episode?>

    /**
     * Insert a podcast in the database. If the podcast already exists, replace it
     */
    fun insertPodcast(podcast: Podcast)

    /**
     * Update a podcast
     */
    fun updatePodcast(podcast: Podcast)

    /**
     * Delete a podcast
     */
    fun deletePodcast(podcast: Podcast)

    /**
     * Update podcast
     */
    fun updatePodcastFromFeedlyApi(podcast: Podcast): Deferred<Boolean>
}