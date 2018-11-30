package com.caldeirasoft.basicapp.ui.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.repository.PodcastItunesDataSourceFactory
import com.caldeirasoft.basicapp.data.repository.PodcastRepository
import com.caldeirasoft.basicapp.ui.common.SingleLiveEvent
import com.caldeirasoft.basicapp.ui.viewmodel.PagedListViewModel
import com.caldeirasoft.basicapp.util.LoadingState
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CatalogViewModel : PagedListViewModel<Podcast>() {

    lateinit private var mainThreadExecutor: Executor
    private var ioExecutor: ExecutorService


    var catalogCategory = MutableLiveData<Int>()
    var podcastRepository: PodcastRepository
    lateinit var sourceFactory: PodcastItunesDataSourceFactory

    var descriptionLoadingState
            = MutableLiveData<Boolean>().apply { value = false }

    lateinit override var loadingState : LiveData<LoadingState>
    lateinit override var data: LiveData<PagedList<Podcast>>

    internal val openPodcastEvent = SingleLiveEvent<Podcast>()
    internal val openLastEpisodeEvent = SingleLiveEvent<Episode>()
    internal val updatePodcastEvent = SingleLiveEvent<Podcast>()
    internal val updatePodcastDescriptionEvent = SingleLiveEvent<Podcast>()
    internal val updatePodcastSubscriptionEvent = SingleLiveEvent<Podcast>()

    /**
     * Default constructor
     */
    init {
        podcastRepository = PodcastRepository()
        ioExecutor = Executors.newFixedThreadPool(5)
    }

    /**
     * Set catalog category and set podcast catalog list
     */
    fun setCategory(code:Int) {
        catalogCategory.postValue(code)
        sourceFactory = podcastRepository.getPodcastsDataSourceFactoryFromItunes(code)

        loadingState = switchMap(sourceFactory.sourceLiveData, {it.loadingState})

        val pagedListConfig = PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(true)
                .setPrefetchDistance(5)
                .setInitialLoadSizeHint(40)
                .build()
        data =  LivePagedListBuilder(sourceFactory, pagedListConfig)
                .setFetchExecutor(ioExecutor)
                .build()
    }

    /**
     * Refresh source factory
     */
    fun refresh() =
        sourceFactory.sourceLiveData.value?.invalidate()


    fun getPodcastFromItunesId(trackId: Int): Podcast? =
        data.value?.find { podcast -> podcast.trackId == trackId }


    /**
     * Retrieve podcast description
     */
    fun getDescription(podcast:Podcast)
    {
        descriptionLoadingState.postValue(true)

        podcastRepository.updatePodcastFromFeedlyApi(podcast)
        updatePodcastDescriptionEvent.value = podcast

        descriptionLoadingState.postValue(false)
    }


    fun getLastEpisode(podcast:Podcast)
    {
        podcastRepository.getLastEpisode(podcast, { episode ->
            openLastEpisodeEvent.value = episode
        })
    }

    fun onPodcastClick(podcast:Podcast)
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
        podcast.apply {
            when (isInDatabase)
            {
                false -> { // not yet in database : subscribe
                    podcastRepository.insertPodcast(this)
                    isInDatabase = true
                }
                true -> { // in database : remove
                    podcastRepository.deletePodcast(this)
                    isInDatabase = false
                }
            }

            updatePodcastEvent.value = this
            updatePodcastSubscriptionEvent.value = this
        }
    }

    companion object {
        val PAGE_SIZE = 15
    }
}