package com.caldeirasoft.basicapp.ui.podcastinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.repository.*
import com.caldeirasoft.basicapp.ui.common.SingleLiveEvent
import com.caldeirasoft.basicapp.util.LoadingState
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class PodcastInfoViewModel : ViewModel() {

    lateinit private var mainThreadExecutor: Executor
    private var ioExecutor: Executor


    lateinit var episodes: LiveData<PagedList<Episode>>
    var podcastRepository: PodcastRepository
    var episodeRepository: EpisodeRepository
    lateinit var episodeFactory: EpisodeFeedlyDataSourceFactory

    var descriptionLoadingState
            = MutableLiveData<Boolean>().apply { value = false }

    lateinit var loadingState: LiveData<LoadingState>

    internal val openPodcastEvent = SingleLiveEvent<Podcast>()
    internal val openLastEpisodeEvent = SingleLiveEvent<Episode>()
    internal val updatePodcastEvent = SingleLiveEvent<Podcast>()
    internal val updatePodcastDescriptionEvent = SingleLiveEvent<Podcast>()
    internal val updatePodcastSubscriptionEvent = SingleLiveEvent<Podcast>()

    init {
        episodeRepository = EpisodeRepository()
        podcastRepository = PodcastRepository()
        ioExecutor = Executors.newFixedThreadPool(5)
    }

    fun setPodcast(feed: Podcast) {
        episodeFactory = episodeRepository.getEpisodeDataSourceFromFeedly(feed)

        loadingState = switchMap(episodeFactory.sourceLiveData, {it.loadingState})

        val pagedListConfig = PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .build()
        episodes =  LivePagedListBuilder(episodeFactory, pagedListConfig)
                .setFetchExecutor(ioExecutor)
                .build()
    }

    fun refresh() =
        episodeFactory.sourceLiveData.value?.invalidate()


    fun onPodcastClick(podcast:Podcast)
    {
        podcast.apply {
            openPodcastEvent.postValue(podcast)
        }
    }

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