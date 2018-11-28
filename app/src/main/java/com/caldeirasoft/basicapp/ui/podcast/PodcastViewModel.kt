package com.caldeirasoft.basicapp.ui.podcast

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import com.caldeirasoft.basicapp.App
import com.caldeirasoft.basicapp.Mockup
import com.caldeirasoft.basicapp.data.db.podcasts.PodcastDataSource
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.repository.PodcastRepository
import com.caldeirasoft.basicapp.ui.base.SingleLiveEvent
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class PodcastViewModel : ViewModel() {

    var podcastRepository: PodcastDataSource
    lateinit private var mainThreadExecutor: Executor
    private var ioExecutor: Executor

    var podcasts: MutableLiveData<List<Podcast>> = MutableLiveData<List<Podcast>>()
    var datasourceFactory: DataSource.Factory<Int, Podcast>

    internal val openPodcastEvent = SingleLiveEvent<Podcast>()
    internal val updatePodcastEvent = SingleLiveEvent<Podcast>()

    init {
        podcastRepository = PodcastRepository()
        ioExecutor = Executors.newFixedThreadPool(5)
        datasourceFactory = podcastRepository.getPodcastsDataSourceFromDb()
        //podcasts = podcastRepository.getPodcastsFromDb()
        Mockup.getPodcasts()?.let {
            podcasts.postValue(it)
        }
    }

    companion object {
        val PAGE_SIZE = 15
    }

    fun refresh() { //datasourceFactory.invalidate()
    }

    fun getPodcastFromItunesId(trackId: Int): Podcast?
    {
        return podcasts.value?.find { podcast -> podcast.trackId == trackId }
    }

    fun onPodcastClick(podcast:Podcast)
    {
        openPodcastEvent.value = podcast
    }

}