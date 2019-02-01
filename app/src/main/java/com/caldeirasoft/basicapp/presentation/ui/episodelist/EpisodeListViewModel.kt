package com.caldeirasoft.basicapp.presentation.ui.episodelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.caldeirasoft.basicapp.domain.entity.Episode
import com.caldeirasoft.basicapp.domain.entity.PodcastWithCount
import com.caldeirasoft.basicapp.domain.entity.SectionState
import com.caldeirasoft.basicapp.presentation.datasource.EpisodeDbDataSourceFactory
import com.caldeirasoft.basicapp.domain.repository.EpisodeRepository
import java.util.concurrent.Executor
import java.util.concurrent.Executors

abstract class EpisodeListViewModel(
        sectionState: SectionState,
        val episodeRepository: EpisodeRepository)
    : ViewModel() {

    var sourceFactory: EpisodeDbDataSourceFactory
    private var ioExecutor: Executor

    var episodes: LiveData<PagedList<Episode>>
    var podcastsWithCount: LiveData<List<PodcastWithCount>>

    init {
        ioExecutor = Executors.newFixedThreadPool(5)
        sourceFactory = EpisodeDbDataSourceFactory(sectionState.value, episodeRepository = episodeRepository)

        val pagedListConfig = PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .build()

        episodes = LivePagedListBuilder(sourceFactory, pagedListConfig)
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

    companion object {
        val PAGE_SIZE = 15
    }
}