package com.caldeirasoft.basicapp.presentation.ui.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.caldeirasoft.basicapp.presentation.utils.SingleLiveEvent
import com.caldeirasoft.castly.data.util.ItunesTopPodcastsDataSource
import com.caldeirasoft.castly.domain.model.entities.Episode
import com.caldeirasoft.castly.domain.model.entities.NetworkState
import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.domain.repository.ItunesRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
    var itunesPodcastDataSourceFactory: DataSource.Factory<Int, Podcast>? = null
    var data: LiveData<PagedList<Podcast>>? = null
    var dataItems: MutableLiveData<List<Podcast>> = MutableLiveData()

    private var networkState = MutableLiveData<NetworkState>()
    val networkStateLiveData = networkState as LiveData<NetworkState>

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

        isLoading = MutableLiveData()
        networkErrors = MutableLiveData()

        val pagedListConfig = PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(true)
                .setPrefetchDistance(5)
                .setInitialLoadSizeHint(40)
                .build()
        /*
        data =  LivePagedListBuilder(itunesPodcastDataSourceFactory, pagedListConfig)
                //.setBoundaryCallback()
                .setFetchExecutor(ioExecutor)
                .build()

         */
    }


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
                /*
                when (savingStatus) {
                    SavingState.AVAILABLE -> { // not yet in database : subscribe
                        this.savingStatus = SavingState.LOADING
                        podcastRepository.insert(this)
                        //isInDatabase = true
                    }
                    SavingState.SAVED -> { // in database : remove
                        this.savingStatus = SavingState.LOADING
                        podcastRepository.delete(this)
                        this.savingStatus = SavingState.AVAILABLE
                    }
                }
                */

                updatePodcastEvent.value = this
                updatePodcastSubscriptionEvent.value = this
            }
        }
    }

    companion object {
        val PAGE_SIZE = 15
    }
}