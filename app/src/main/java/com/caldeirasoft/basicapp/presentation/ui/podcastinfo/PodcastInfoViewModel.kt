package com.caldeirasoft.basicapp.presentation.ui.podcastinfo

import androidx.lifecycle.*
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.caldeirasoft.basicapp.presentation.datasource.EpisodeDbDataSourceFactory
import com.caldeirasoft.basicapp.presentation.datasource.EpisodeFeedlyDataSourceFactory
import com.caldeirasoft.basicapp.domain.entity.Episode
import com.caldeirasoft.basicapp.domain.entity.Podcast
import com.caldeirasoft.basicapp.domain.entity.SectionWithCount
import com.caldeirasoft.basicapp.domain.entity.SectionState
import com.caldeirasoft.basicapp.domain.entity.SubscribeAction
import com.caldeirasoft.basicapp.domain.repository.EpisodeRepository
import com.caldeirasoft.basicapp.domain.repository.FeedlyRepository
import com.caldeirasoft.basicapp.domain.repository.PodcastRepository
import com.caldeirasoft.basicapp.domain.usecase.*
import com.caldeirasoft.basicapp.presentation.datasource.EpisodePodcastDataSourceFactory
import com.caldeirasoft.basicapp.presentation.utils.SingleLiveEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class PodcastInfoViewModel(
        val podcast: Podcast,
        val podcastRepository: PodcastRepository,
        val episodeRepository: EpisodeRepository,
        val getEpisodesFromFeedlyUseCase: GetEpisodesFromFeedlyUseCase,
        val getPodcastFromFeedlyUseCase: GetPodcastFromFeedlyUseCase,
        val subscribeToPodcastUseCase: SubscribeToPodcastUseCase,
        val unsubscribeFromPodcastUseCase: UnsubscribeFromPodcastUseCase)
    : ViewModel() {

    lateinit var podcastInDb: LiveData<Podcast>
    lateinit var episodeCountBySection: LiveData<SectionWithCount>
    internal val subscribePodcastEvent = SingleLiveEvent<Podcast>()
    internal val addEpisodeToInboxEvent = SingleLiveEvent<Boolean>()
    internal val openEpisodeEvent = SingleLiveEvent<Episode>()
    internal val updateEpisodeEvent = SingleLiveEvent<Episode>()
    lateinit private var mainThreadExecutor: Executor
    private var ioExecutor: Executor

    var isInDatabase: MediatorLiveData<Boolean> = MediatorLiveData()

    var isSubscribing = MutableLiveData<Boolean>()

    var podcastData = MutableLiveData<Podcast>().apply { value = podcast }

    var sectionData = MutableLiveData<Int>()

    var descriptionData: MutableLiveData<String> = MutableLiveData()

    val episodePodcastDataSourceFactory
            = EpisodePodcastDataSourceFactory(podcast, sectionData.value, episodeRepository, getEpisodesFromFeedlyUseCase)

    val isLoading: LiveData<Boolean> by lazy {
        Transformations.map(episodePodcastDataSourceFactory.isLoading) { it }
    }

    var pagedList: LiveData<PagedList<Episode>>
    val episodes: LiveData<PagedList<Episode>>
        get() = pagedList

    init {
        ioExecutor = Executors.newFixedThreadPool(5)

        // podcast in database
        val podcastInDb = podcastRepository.getPodcastById(podcast.feedUrl)
        isInDatabase.addSource(podcastInDb) { pod ->
            isInDatabase.value = pod != null
        }

        // pagedList
        val pagedListConfig: PagedList.Config = PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .build()

        // pagedList
        pagedList = LivePagedListBuilder(episodePodcastDataSourceFactory, pagedListConfig)
                    .setFetchExecutor(ioExecutor)
                    .build()

        // get podcast info
        getDescriptionInfo()
    }

    companion object {
        val PAGE_SIZE = 15
    }

    /**
     * Get podcast description info
     */
    fun getDescriptionInfo() {
        GlobalScope.launch {
            podcast.description
                    ?.let {
                        descriptionData.postValue(it)
                    }
                    ?: run {
                        // get podcast from db
                        getPodcastFromFeedlyUseCase
                                .execute(GetPodcastFromFeedlyUseCase.Params(podcast.feedUrl))
                                .await()
                                .data
                                ?.let {
                                    descriptionData.postValue(it.description)
                                }
                    }
        }
    }

    // change episode filter (by section)
    fun setSection(sec: Int?) {
        sectionData.postValue(sec)
    }

    fun refresh() =
            episodes.value?.dataSource?.invalidate()

    fun openEpisodeDetail(episode: Episode) {
        openEpisodeEvent.value = episode
    }

    fun subscribe() {
        GlobalScope.launch {
            subscribeToPodcastUseCase.beforeExecute = { isSubscribing.postValue(true) }
            subscribeToPodcastUseCase.afterExecute = { isSubscribing.postValue(false) }
            subscribeToPodcastUseCase.failed = { it.printStackTrace() }
            subscribeToPodcastUseCase.execute(SubscribeToPodcastUseCase.Params(podcast)).await()
        }
    }

    fun unsubscribe() {
        GlobalScope.launch {
            unsubscribeFromPodcastUseCase.beforeExecute = { isSubscribing.postValue(true) }
            unsubscribeFromPodcastUseCase.afterExecute = { isSubscribing.postValue(false) }
            unsubscribeFromPodcastUseCase.failed = { it.printStackTrace() }
            unsubscribeFromPodcastUseCase.execute(UnsubscribeFromPodcastUseCase.Params(podcast)).await()
        }
    }
}