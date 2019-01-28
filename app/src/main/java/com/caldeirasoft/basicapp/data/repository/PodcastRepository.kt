package com.caldeirasoft.basicapp.data.repository

import androidx.lifecycle.*
import androidx.paging.*
import android.util.Log
import com.avast.android.githubbrowser.extensions.retrofitCallback
import com.caldeirasoft.basicapp.App
import com.caldeirasoft.basicapp.data.db.podcasts.PodcastDataSource
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.api.feedly.retrofit.FeedlyAPI
import com.caldeirasoft.basicapp.api.itunes.retrofit.ITunesAPI
import com.caldeirasoft.basicapp.api.rss.retrofit.RssAPI
import com.caldeirasoft.basicapp.data.db.AppDatabase
import com.caldeirasoft.basicapp.data.entity.Episode
import com.github.salomonbrys.kodein.LazyKodein
import com.github.salomonbrys.kodein.LazyKodeinAware
import com.github.salomonbrys.kodein.instance
import kotlinx.coroutines.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult

/**
 * Created by Edmond on 15/02/2018.
 */
class PodcastRepository : PodcastDataSource, LazyKodeinAware
{
    private val TAG = PodcastRepository::class.java.simpleName

    override val kodein = LazyKodein { App.context!!.kodein }
    private val database: AppDatabase by instance()
    private val feedlyAPI: FeedlyAPI by instance()
    private val iTunesAPI: ITunesAPI by instance()
    private val rssAPI: RssAPI by instance()
    private val podcastDao = database.podcastDao()

    override fun getPodcastsFromDb(): LiveData<List<Podcast>> =
        podcastDao.getPodcasts()


    override fun getPodcastById(feedUrl:String): LiveData<Podcast> = podcastDao.getPodcastById(feedUrl)

    /**
     * Select a podcast by id
     */
    override fun getPodcastFromFeedlyApi(feedUrl: String): Deferred<Podcast?> =
        // request Ids
        GlobalScope.async {
            val feedId = "feed/" + feedUrl
            var feed = feedlyAPI.getFeed(feedId).await();
            val podcast = Podcast()
            //podcast.imageUrl = feed.artwork
            podcast.feedUrl = feedUrl
            podcast.updated = feed.updated
            podcast.description = feed.description
            podcast
        }

    /**
     * Select all podcasts from catalog
     */
    override fun getPodcastsFromItunesCatalog(pageSize: Int): LiveData<PagedList<Podcast>>
    {
        val pageSize = 20
        val sourceFactory = PodcastItunesDataSourceFactory(itunesApi = iTunesAPI, podcastDao = database.podcastDao(), genre = 26)
        val pagedList = LivePagedListBuilder(sourceFactory, pageSize)
            //.setBackgroundThreadExecutor(ioExecutor)
            .build()
        return pagedList
    }

    override fun getPodcastsDataSourceFromDb(): DataSource.Factory<Int, Podcast>
    {
        val source = database.podcastDao().getPodcastDataSource()
        return source
    }

    /**
     * Get itunes store front
     */
    override fun getItunesStoreSourceFactory(storeFront: String): ItunesStoreSourceFactory {
        val store = ItunesStoreSourceFactory(itunesApi = iTunesAPI, podcastDao = database.podcastDao(), storeFront = storeFront)
        return store;
    }

    /**
     * Get itunes podcasts data source from a list of ids
     */
    override fun getItunesLookupDataSourceFactory(ids: List<Int>): ItunesLookupDataSourceFactory {
        val sourceFactory = ItunesLookupDataSourceFactory(itunesApi = iTunesAPI, podcastDao = database.podcastDao(), ids = ids)
        return sourceFactory;
    }

    /**
     * Get all podcasts data source from catalog
     */
    override fun getPodcastsDataSourceFactoryFromItunes(genre: Int): PodcastItunesDataSourceFactory
    {
        val sourceFactory = PodcastItunesDataSourceFactory(iTunesAPI, database.podcastDao(), genre);
        return sourceFactory
    }

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
            database.podcastDao().updatePodcast(podcast)
        }
    }

    /**
     * Delete a podcast
     */
    override fun deletePodcast(podcast: Podcast) {
        doAsync {
            database.podcastDao().deletePodcast(podcast)
        }
    }

    override fun getLastEpisode(podcast: Podcast): Deferred<Episode?> =
        GlobalScope.async {
            val streamEntries = feedlyAPI.getStreamEntries(podcast.feedId, 1, "").await()
            streamEntries.items
                    .filter { entry ->
                        entry.enclosure.firstOrNull()?.href != null
                    }
                    .map { entry ->
                        Episode(entry.id
                                , entry.originId ?: ""
                                , entry.title
                                , entry.published ?: 0)
                                .apply {
                                    description = entry.summary?.content
                                    mediaUrl = entry.enclosure.get(0).href
                                    mediaType = entry.enclosure.get(0).type
                                    mediaLength = entry.enclosure.get(0).length
                                    link = entry.origin.htmlUrl
                                }
                    }
                    .firstOrNull()

        }

    /**
     * Update podcast
     */
    override fun updatePodcastFromFeedlyApi(podcast: Podcast): Deferred<Boolean> =
        GlobalScope.async {
            val result = getPodcastFromFeedlyApi(podcast.feedUrl).await()
            result?.let {
                podcast.updated = it.updated
                podcast.description = it.description
                true
            } ?: false
        }

}