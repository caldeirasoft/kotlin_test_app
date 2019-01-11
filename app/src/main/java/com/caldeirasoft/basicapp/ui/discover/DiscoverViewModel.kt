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
    private var podcastRepository: PodcastRepository
    private var sourceFactory: ItunesStoreSourceFactory

    var descriptionLoadingState
            = MutableLiveData<Boolean>().apply { value = false }

    var itunesStore: LiveData<ItunesStore>
    lateinit var loadingState : LiveData<LoadingState>

    /**
     * Default constructor
     */
    init {
        podcastRepository = PodcastRepository()
        ioExecutor = Executors.newFixedThreadPool(5)

        sourceFactory = podcastRepository.getItunesStoreSourceFactory("143442-3,31")
        itunesStore = map(sourceFactory.itunesStore) { it }
    }

    fun request() {
        if (!isDiscoverLoaded) {
            sourceFactory.request()
            isDiscoverLoaded = true
        }
    }
}