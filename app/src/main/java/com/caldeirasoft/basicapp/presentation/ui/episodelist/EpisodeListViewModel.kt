package com.caldeirasoft.basicapp.presentation.ui.episodelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.basicapp.presentation.datasource.EpisodeDbDataSourceFactory
import com.caldeirasoft.basicapp.presentation.ui.base.MediaItemViewModel
import com.caldeirasoft.castly.domain.model.MediaID
import com.caldeirasoft.castly.domain.model.PodcastWithCount
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import java.util.concurrent.Executor
import java.util.concurrent.Executors

abstract class EpisodeListViewModel(
        sectionState: SectionState,
        mediaSessionConnection: MediaSessionConnection,
        episodeRepository: EpisodeRepository)
    : MediaItemViewModel<Episode>(MediaID(sectionState).asString(), mediaSessionConnection) {

    fun applyFilter(feedUrl: String?) {
        //sourceFactory.applyFilter(feedUrl)
        //episodes.value?.dataSource?.invalidate()
    }
}