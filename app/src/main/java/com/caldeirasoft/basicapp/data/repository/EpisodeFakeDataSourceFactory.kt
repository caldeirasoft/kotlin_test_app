package com.caldeirasoft.basicapp.data.repository

import androidx.lifecycle.*
import androidx.paging.*
import com.avast.android.githubbrowser.extensions.retrofitCallback
import com.caldeirasoft.basicapp.api.feedly.retrofit.FeedlyAPI
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.api.itunes.retrofit.ITunesAPI
import com.caldeirasoft.basicapp.data.db.episodes.EpisodeDao
import com.caldeirasoft.basicapp.data.db.podcasts.PodcastDao
import com.caldeirasoft.basicapp.data.entity.Episode
import org.jetbrains.anko.doAsync
import java.util.concurrent.Executor

/**
 * Created by Edmond on 15/02/2018.
 */
class EpisodeFakeDataSourceFactory(
        private var feed: Podcast,
        val feedlyAPI: FeedlyAPI,
        val episodeDao: EpisodeDao
) : DataSource.Factory<Int, Episode>()
{
    val sourceLiveData = MutableLiveData<EpisodeFakeDataSource>()

    override fun create(): DataSource<Int, Episode> {
        val source = EpisodeFakeDataSource(feed, feedlyAPI, episodeDao)
        sourceLiveData.postValue(source)
        return source
    }

    fun applyPodcast(feed: Podcast) {
        this.feed = feed
    }

}