package com.caldeirasoft.basicapp.data.repository

import androidx.lifecycle.*
import androidx.lifecycle.Transformations.switchMap
import androidx.paging.*
import com.avast.android.githubbrowser.extensions.retrofitCallback
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.api.itunes.retrofit.ITunesAPI
import com.caldeirasoft.basicapp.data.db.podcasts.PodcastDao
import org.jetbrains.anko.doAsync
import java.util.concurrent.Executor

/**
 * Created by Edmond on 15/02/2018.
 */
class PodcastItunesDataSourceFactory(
        val itunesApi: ITunesAPI,
        val podcastDao: PodcastDao,
        val genre: Int
) : DataSource.Factory<Int, Podcast>()
{
    val sourceLiveData = MutableLiveData<PodcastItunesDataSource>()

    override fun create(): DataSource<Int, Podcast> {
        val source = PodcastItunesDataSource(itunesApi, podcastDao, genre)
        sourceLiveData.postValue(source)
        return source
    }
}