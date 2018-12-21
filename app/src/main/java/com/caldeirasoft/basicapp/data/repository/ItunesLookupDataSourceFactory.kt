package com.caldeirasoft.basicapp.data.repository

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.avast.android.githubbrowser.extensions.retrofitCallback
import com.caldeirasoft.basicapp.api.itunes.retrofit.ITunesAPI
import com.caldeirasoft.basicapp.data.db.podcasts.PodcastDao
import com.caldeirasoft.basicapp.data.entity.Podcast
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.withAlpha
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Edmond on 09/02/2018.
 */
class ItunesLookupDataSourceFactory(
        val itunesApi: ITunesAPI,
        val podcastDao: PodcastDao,
        val ids:List<Int>)
    : DataSource.Factory<Int, Podcast>()
{
    val sourceLiveData = MutableLiveData<ItunesLookupDataSource>()

    override fun create(): DataSource<Int, Podcast> {
        val source = ItunesLookupDataSource(itunesApi, podcastDao, ids)
        sourceLiveData.postValue(source)
        return source
    }
}