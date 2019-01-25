package com.caldeirasoft.basicapp.ui.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel
import com.caldeirasoft.basicapp.data.repository.*
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DiscoverViewModel : ViewModel() {

    lateinit private var mainThreadExecutor: Executor
    private var ioExecutor: ExecutorService
    private var isDiscoverLoaded = false
    private var podcastRepository: PodcastRepository
    private var sourceFactory: ItunesStoreSourceFactory

    var itunesStore: LiveData<ItunesStore>
    val isLoading: LiveData<Boolean>

    /**
     * Default constructor
     */
    init {
        podcastRepository = PodcastRepository()
        ioExecutor = Executors.newFixedThreadPool(5)

        sourceFactory = podcastRepository.getItunesStoreSourceFactory("143442-3,31")
        itunesStore = map(sourceFactory.itunesStore) { it }
        isLoading = map(sourceFactory.isLoading) { it }
    }

    fun request() {
        if (!isDiscoverLoaded) {
            sourceFactory.request()
            isDiscoverLoaded = true
        }
    }
}