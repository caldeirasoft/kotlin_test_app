package com.caldeirasoft.basicapp.presentation.ui.podcastinfo

import androidx.lifecycle.*
import androidx.media2.MediaItem
import androidx.media2.MediaMetadata
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.model.SectionWithCount
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.basicapp.presentation.datasource.EpisodePodcastDataSourceFactory
import com.caldeirasoft.basicapp.presentation.datasource.MediaItemDataSourceFactory
import com.caldeirasoft.basicapp.presentation.utils.SingleLiveEvent
import com.caldeirasoft.castly.domain.model.MediaID
import com.caldeirasoft.castly.domain.usecase.GetEpisodesFromFeedlyUseCase
import com.caldeirasoft.castly.domain.usecase.GetPodcastFromFeedlyUseCase
import com.caldeirasoft.castly.domain.usecase.SubscribeToPodcastUseCase
import com.caldeirasoft.castly.domain.usecase.UnsubscribeUseCase
import com.caldeirasoft.castly.service.playback.PodcastLibraryService.Companion.TYPE_PODCAST
import com.caldeirasoft.castly.service.playback.extensions.toPodcast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class PodcastInfoViewModel(
        val mediaMetadata: MediaMetadata,
        val podcastRepository: PodcastRepository,
        val episodeRepository: EpisodeRepository,
        val getEpisodesFromFeedlyUseCase: GetEpisodesFromFeedlyUseCase,
        val getPodcastFromFeedlyUseCase: GetPodcastFromFeedlyUseCase,
        val subscribeToPodcastUseCase: SubscribeToPodcastUseCase,
        val unsubscribeUseCase: UnsubscribeUseCase,
        val mediaSessionConnection: MediaSessionConnection)
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

    var podcastData = MutableLiveData<Podcast>()

    var sectionData = MutableLiveData<Int>()

    var descriptionData: MutableLiveData<String> = MutableLiveData()

    val episodePodcastDataSourceFactory
            = EpisodePodcastDataSourceFactory(podcastInDb.value!!, sectionData.value, episodeRepository, getEpisodesFromFeedlyUseCase)

    val isLoading: LiveData<Boolean> by lazy {
        Transformations.switchMap(episodePodcastDataSourceFactory.sourceLiveData) { it.isLoading }
    }

    var pagedList: LiveData<PagedList<Episode>>
    val episodes: LiveData<PagedList<Episode>>
        get() = pagedList

    //region media
    private val mediaId: MediaID = MediaID(TYPE_PODCAST, mediaMetadata.mediaId)

    //mediaItems
    fun getPagedMediaItems(): LiveData<PagedList<MediaItem>> {
        val pagedListConfig: PagedList.Config = PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .build()
        // pagedList
        return LivePagedListBuilder(MediaItemDataSourceFactory(mediaId.asString(), mediaMetadata, mediaSessionConnection), pagedListConfig)
                .setFetchExecutor(ioExecutor)
                .build()
    }

    /*
    // mediasession
    private val mediaSessionConnection = mediaSessionConnection.also {
        it.subscribe(mediaId.asString(), subscriptionCallback)
    }
    */

    //endregion

    companion object {
        val PAGE_SIZE = 15
    }

    init {
        ioExecutor = Executors.newFixedThreadPool(5)

        // podcast
        podcastData.postValue(mediaMetadata.toPodcast())

        // podcast in database
        val podcastInDb = podcastRepository.get(mediaMetadata.mediaId.toString())
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

    /**
     * Get podcast description info
     */
    fun getDescriptionInfo() {
        GlobalScope.launch {
            podcastInDb.value
                    ?.description
                    ?.let {
                        descriptionData.postValue(it)
                    }
                    ?: run {
                        // get podcast from db
                        getPodcastFromFeedlyUseCase
                                .execute(GetPodcastFromFeedlyUseCase.Params(mediaMetadata.mediaId.toString()))
                                .await()
                                .data
                                ?.let {
                                    descriptionData.postValue(it.description)
                                }
                    }
        }
    }

    //hacky way to force reload items (e.g. song sort order changed)
    /*
    fun reloadMediaItems() {
        mediaSessionConnection.unsubscribe(mediaId.asString(), subscriptionCallback)
        mediaSessionConnection.subscribe(mediaId.asString(), subscriptionCallback)
    }
    */

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
            subscribeToPodcastUseCase.execute(SubscribeToPodcastUseCase.Params(podcastInDb.value!!)).await()
        }
    }

    fun unsubscribe() {
        GlobalScope.launch {
            unsubscribeUseCase.beforeExecute = { isSubscribing.postValue(true) }
            unsubscribeUseCase.afterExecute = { isSubscribing.postValue(false) }
            unsubscribeUseCase.failed = { it.printStackTrace() }
            unsubscribeUseCase.execute(UnsubscribeUseCase.Params(podcastInDb.value!!)).await()
        }
    }
}