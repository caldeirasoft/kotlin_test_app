package com.caldeirasoft.basicapp.ui.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel
import com.caldeirasoft.basicapp.data.repository.*
import com.caldeirasoft.basicapp.util.LoadingState
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DiscoverViewModel : ViewModel() {

    lateinit private var mainThreadExecutor: Executor
    private var ioExecutor: ExecutorService
    private var isDiscoverLoaded = false

    var catalogCategory = MutableLiveData<Int>()
    var podcastRepository: PodcastRepository
    var itunesStore: ItunesStoreSourceFactory
    lateinit var sourceFactory: PodcastItunesDataSourceFactory

    var descriptionLoadingState
            = MutableLiveData<Boolean>().apply { value = false }

    var trendingPodcasts: LiveData<List<PodcastArtwork>>
    var podcastGroups: LiveData<List<ItunesSection>>
    lateinit var loadingState : LiveData<LoadingState>

    /**
     * Default constructor
     */
    init {
        podcastRepository = PodcastRepository()
        ioExecutor = Executors.newFixedThreadPool(5)

        itunesStore = podcastRepository.getItunesStoreSourceFactory("143442-3,31")
        trendingPodcasts = map(itunesStore.trendingPodcasts) { it }
        podcastGroups = map(itunesStore.itunesSections) { it }
    }

    fun request() {
        if (!isDiscoverLoaded) {
            itunesStore.request()
            isDiscoverLoaded = true
        }
    }

    companion object {
        val PAGE_SIZE = 15
    }
}