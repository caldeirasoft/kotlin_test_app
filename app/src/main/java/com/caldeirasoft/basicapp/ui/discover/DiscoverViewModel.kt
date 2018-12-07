package com.caldeirasoft.basicapp.ui.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.repository.*
import com.caldeirasoft.basicapp.ui.common.SingleLiveEvent
import com.caldeirasoft.basicapp.util.LoadingState
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DiscoverViewModel : ViewModel() {

    lateinit private var mainThreadExecutor: Executor
    private var ioExecutor: ExecutorService

    var catalogCategory = MutableLiveData<Int>()
    var podcastRepository: PodcastRepository
    var itunesStore: ItunesStore
    lateinit var sourceFactory: PodcastItunesDataSourceFactory

    var descriptionLoadingState
            = MutableLiveData<Boolean>().apply { value = false }

    var trendingPodcasts: LiveData<List<PodcastArtwork>>
    var podcastGroups: LiveData<List<PodcastGroup>>
    lateinit var loadingState : LiveData<LoadingState>

    /**
     * Default constructor
     */
    init {
        podcastRepository = PodcastRepository()
        ioExecutor = Executors.newFixedThreadPool(5)

        itunesStore = podcastRepository.getPodcastsPreviewFromItunes("143442-3,30")
        trendingPodcasts = map(itunesStore.liveTrendingPodcasts) { it }
        podcastGroups = map(itunesStore.livePodcastGroups) { it }
    }

    fun request() {
        itunesStore.request()
    }

    fun requestGroup(group: PodcastGroup) {
        itunesStore.requestGroup(group)
    }

    companion object {
        val PAGE_SIZE = 15
    }
}