package com.caldeirasoft.basicapp.ui.podcastdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.entity.SectionWithCount
import com.caldeirasoft.basicapp.data.enum.SectionState
import com.caldeirasoft.basicapp.data.enum.SubscribeAction
import com.caldeirasoft.basicapp.data.repository.*
import com.caldeirasoft.basicapp.ui.common.SingleLiveEvent
import com.caldeirasoft.basicapp.util.LoadingState
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class PodcastDetailViewModel() : ViewModel() {

    lateinit private var mainThreadExecutor: Executor
    private var ioExecutor: Executor

    private var mPodcast: Podcast = Podcast.DEFAULT_PODCAST
    private var mSection:Int? = null

    var podcast = MutableLiveData<Podcast>()
    var section = MutableLiveData<Int>()
    var isInDatabase = MutableLiveData<Boolean>()

    lateinit var loadingState: LiveData<LoadingState>
    lateinit var durationEvent: LiveData<Episode>

    lateinit var podcastInDb : LiveData<Podcast>
    lateinit var episodes: LiveData<PagedList<Episode>>
    lateinit var episodesFromDb: LiveData<List<Episode>>
    lateinit var episodeCountBySection: LiveData<SectionWithCount>

    var podcastRepository: PodcastRepository
    var episodeRepository : EpisodeRepository
    var episodeDbDataSourceFactory: EpisodeDbDataSourceFactory? = null
    var episodeFeedlyDataSourceFactory: EpisodeFeedlyDataSourceFactory? = null
    lateinit var sourceFactory:  DataSource.Factory<Int, Episode>
    lateinit var pagedListConfig: PagedList.Config

    internal val subscribePodcastEvent = SingleLiveEvent<Podcast>()
    internal val addEpisodeToInboxEvent = SingleLiveEvent<Boolean>()
    internal val openEpisodeEvent = SingleLiveEvent<Episode>()
    internal val updateEpisodeEvent = SingleLiveEvent<Episode>()

    init {
        podcastRepository = PodcastRepository()
        episodeRepository = EpisodeRepository()
        ioExecutor = Executors.newFixedThreadPool(5)
    }

    companion object {
        val PAGE_SIZE = 15
    }

    fun setDataSource(pod:Podcast) =
        setDataSource(pod, null)

    /**
     * Set section and podcast for current viewModel
     */
    fun setDataSource(pod:Podcast, sec: Int?)
    {
        // set section
        mSection = sec
        section.postValue(sec)

        // set podcast
        mPodcast = pod
        podcast.postValue(pod)

        // refresh list
        setEpisodesList(pod, sec)

        // set episodes count by section
        episodeCountBySection = episodeRepository.fetchEpisodeCountBySection(pod.feedUrl)

        // get podcast from db
        podcastInDb = podcastRepository.getPodcastById(pod.feedUrl)
        podcastInDb.observeForever { pcst ->
            isInDatabase.postValue((pcst != null))
            when (pcst == null) {
                true -> {
                    podcastRepository.updatePodcastFromFeedlyApi(pod)
                    mPodcast = pod
                }
            }
        }
    }

    private fun setEpisodesList(pod:Podcast, sec: Int?)
    {
        // data source factory
        episodeDbDataSourceFactory = episodeRepository.getEpisodeDbDataSource(0)
        episodeFeedlyDataSourceFactory = episodeRepository.getEpisodeDataSourceFromFeedly()

        episodeFeedlyDataSourceFactory?.sourceLiveData!!.let {
            // loading state mapping
            loadingState = Transformations.switchMap(it) { it.loadingState }

            //episode duration update mapping
            durationEvent = Transformations.switchMap(it) { it.durationEvent }

            durationEvent.observeForever { episode ->
                updateEpisodeEvent.postValue(episode)
            }
        }

        sourceFactory = object : DataSource.Factory<Int, Episode>() {
            override fun create(): DataSource<Int, Episode> =
                    when (mSection) {
                        null -> episodeFeedlyDataSourceFactory!!.create()
                        else -> episodeDbDataSourceFactory!!.create()
                    }
        }

        pagedListConfig = PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .build()

        episodes = LivePagedListBuilder(sourceFactory, pagedListConfig)
                .setFetchExecutor(ioExecutor)
                .build()
    }

    // get episodes count by section
    fun fetchEpisodeCountBySection(pod: Podcast) =
            episodeRepository.fetchEpisodeCountBySection(pod.feedUrl)

    fun loadMore() {
        pagedListConfig = PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .build()

        episodes = LivePagedListBuilder(sourceFactory, pagedListConfig)
                .setFetchExecutor(ioExecutor)
                .build()
    }

    // change episode filter (by section)
    fun setSection(sec: Int?) {
        val oldSec = mSection
        mSection = sec
        section.postValue(sec)

        if (sec != null) {
            // changement de section
            episodeDbDataSourceFactory?.applySection(sec!!)
        }
        if (::episodes.isInitialized) {
            refresh()
        }
    }

    fun refresh() =
            episodes.value?.dataSource?.invalidate()

    fun openEpisodeDetail(episode:Episode) {
        openEpisodeEvent.value = episode
    }

    fun onPodcastSubscribe() =
        subscribePodcastEvent.postValue(mPodcast)

    fun subscribeToPodcast(podcast:Podcast, action: SubscribeAction)
    {
        podcast.subscribeAction = action.value
        podcastRepository.insertPodcast(podcast)
        when (action) {
            SubscribeAction.INBOX -> {
                addEpisodeToInboxEvent.postValue(true)
            }
            else -> {}
        }
    }

    /**
     * Unsubscribe from podcast
     */
    fun unsubscribeFromPodcast(podcast:Podcast) {
        podcastRepository.deletePodcast(podcast)
        episodeRepository.deleteEpisodes(podcast.feedUrl)
    }

    /**
     * Add first episode avec episode
     */
    fun addFirstEpisodeToInbox(episode: Episode) {
        episode.section = SectionState.INBOX.value
        episodeRepository.insertEpisode(episode)
    }

    /**
     * Archive episode
     */
    fun archiveEpisode(episode: Episode)
    {
        episodeRepository.archiveEpisode(episode)
        updateEpisodeEvent.postValue(episode)
    }

    /**
     * Toggle episode favorite status
     */
    fun toggleEpisodeFavorite(episode: Episode)
    {
        episodeRepository.toggleEpisodeFavorite(episode)
        updateEpisodeEvent.postValue(episode)
    }

    /**
     * Queue episode first
     */
    fun queueEpisodeFirst(episode: Episode)
    {
        episodeRepository.queueEpisodeFirst(episode)
        updateEpisodeEvent.postValue(episode)
    }

    /**
     * Queue episode last
     */
    fun queueEpisodeLast(episode: Episode)
    {
        episodeRepository.queueEpisodeLast(episode)
        updateEpisodeEvent.postValue(episode)
    }

    /**
     * Play episode
     */
    fun playEpisode(episode: Episode)
    {
        episodeRepository.playEpisode(episode)
        updateEpisodeEvent.postValue(episode)
    }
}