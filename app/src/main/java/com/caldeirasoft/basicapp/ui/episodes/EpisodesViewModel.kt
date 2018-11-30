package com.caldeirasoft.basicapp.ui.episodes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.caldeirasoft.basicapp.data.db.episodes.EpisodeDataSource
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.entity.PodcastWithCount
import com.caldeirasoft.basicapp.data.enum.SectionState
import com.caldeirasoft.basicapp.data.repository.EpisodeDbDataSourceFactory
import com.caldeirasoft.basicapp.data.repository.EpisodeRepository
import com.caldeirasoft.basicapp.ui.common.SingleLiveEvent
import java.util.concurrent.Executor
import java.util.concurrent.Executors

abstract class EpisodesViewModel(protected val sectionState: SectionState) : ViewModel() {

    var episodeRepository: EpisodeDataSource
    var sourceFactory: EpisodeDbDataSourceFactory
    lateinit private var mainThreadExecutor: Executor
    private var ioExecutor: Executor

    var episodes: LiveData<PagedList<Episode>>
    var podcastsWithCount: LiveData<List<PodcastWithCount>>
    internal val openPodcastEvent = SingleLiveEvent<Podcast>()
    internal val updatePodcastEvent = SingleLiveEvent<Podcast>()

    init {
        ioExecutor = Executors.newFixedThreadPool(5)
        episodeRepository = EpisodeRepository()
        sourceFactory = episodeRepository.getEpisodeDbDataSource(sectionState.value, null)

        val pagedListConfig = PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .build()

        var sourceFactory2 = episodeRepository.getEpisodeDataSourceFromFake(Podcast.DEFAULT_PODCAST)
        episodes = LivePagedListBuilder(sourceFactory2, pagedListConfig)
                .setFetchExecutor(ioExecutor)
                .build()

        podcastsWithCount = episodeRepository.fetchEpisodesCoutByPodcast(sectionState.value)
    }

    fun refresh() { //datasourceFactory.invalidate()
    }


    fun applyFilter(feedUrl: String?) {
        sourceFactory.applyFilter(feedUrl)
        episodes.value?.dataSource?.invalidate()
    }

    fun onPodcastClick(podcast:Podcast)
    {
        openPodcastEvent.value = podcast
    }

    companion object {
        val PAGE_SIZE = 15
    }
}