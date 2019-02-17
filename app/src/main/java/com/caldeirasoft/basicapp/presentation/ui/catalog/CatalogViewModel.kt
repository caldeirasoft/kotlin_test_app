package com.caldeirasoft.basicapp.presentation.ui.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.repository.ItunesRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.basicapp.presentation.datasource.ItunesPodcastDataSourceFactory
import com.caldeirasoft.basicapp.presentation.utils.SingleLiveEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CatalogViewModel(
        val itunesRepository: ItunesRepository,
        val podcastRepository: PodcastRepository,
        val category: Int)
    : ViewModel() {

    lateinit private var mainThreadExecutor: Executor
    private var ioExecutor: ExecutorService


    var isLoading: LiveData<Boolean>
    var networkErrors: LiveData<String>
    var catalogCategory = MutableLiveData<Int>()
    var itunesPodcastDataSourceFactory: ItunesPodcastDataSourceFactory
    var data: LiveData<PagedList<Podcast>>

    internal val openPodcastEvent = SingleLiveEvent<Podcast>()
    internal val openLastEpisodeEvent = SingleLiveEvent<Episode>()
    internal val updatePodcastEvent = SingleLiveEvent<Podcast>()
    internal val updatePodcastDescriptionEvent = SingleLiveEvent<Podcast>()
    internal val updatePodcastSubscriptionEvent = SingleLiveEvent<Podcast>()

    /**
     * Default constructor
     */
    init {
        ioExecutor = Executors.newFixedThreadPool(5)
        itunesPodcastDataSourceFactory = ItunesPodcastDataSourceFactory(itunesRepository, podcastRepository, category)

        isLoading = Transformations.switchMap(itunesPodcastDataSourceFactory.sourceLiveData) { it.isLoading }
        networkErrors = Transformations.switchMap(itunesPodcastDataSourceFactory.sourceLiveData) { it.networkErrors }

        val pagedListConfig = PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(true)
                .setPrefetchDistance(5)
                .setInitialLoadSizeHint(40)
                .build()
        data =  LivePagedListBuilder(itunesPodcastDataSourceFactory, pagedListConfig)
                //.setBoundaryCallback()
                .setFetchExecutor(ioExecutor)
                .build()
    }

    /**
     * Set catalog category and set podcast catalog list
     */
    fun setCategory(code:Int) {
        catalogCategory.postValue(code)
        itunesPodcastDataSourceFactory.applyGenre(code)
    }

    /**
     * Refresh source factory
     */
    fun refresh() = itunesPodcastDataSourceFactory.invalidate()


    fun onPodcastClick(podcast: Podcast)
    {
        podcast.apply {
            openPodcastEvent.postValue(podcast)
        }
    }

    /**
     * Called on podcast subscription/unsubscription
     * add/remove podcast from db and trigger the event
     */
    fun onPodcastSubscribe(podcast: Podcast)
    {
        GlobalScope.launch {
            podcast.apply {
                when (isInDatabase) {
                    false -> { // not yet in database : subscribe
                        podcastRepository.insert(this).await()
                        isInDatabase = true
                    }
                    true -> { // in database : remove
                        podcastRepository.delete(this).await()
                        isInDatabase = false
                    }
                }

                updatePodcastEvent.value = this
                updatePodcastSubscriptionEvent.value = this
            }
        }
    }

    companion object {
        val PAGE_SIZE = 15
    }
}