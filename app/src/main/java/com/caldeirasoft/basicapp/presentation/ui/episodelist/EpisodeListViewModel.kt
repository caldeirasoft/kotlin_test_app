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
import com.caldeirasoft.castly.service.playback.PodcastLibraryService.Companion.TYPE_FAVORITE
import com.caldeirasoft.castly.service.playback.PodcastLibraryService.Companion.TYPE_GENRE
import com.caldeirasoft.castly.service.playback.PodcastLibraryService.Companion.TYPE_HISTORY
import com.caldeirasoft.castly.service.playback.PodcastLibraryService.Companion.TYPE_INBOX
import com.caldeirasoft.castly.service.playback.PodcastLibraryService.Companion.TYPE_QUEUE
import java.util.concurrent.Executor
import java.util.concurrent.Executors

abstract class EpisodeListViewModel(
        sectionState: SectionState,
        mediaSessionConnection: MediaSessionConnection,
        episodeRepository: EpisodeRepository)
    : MediaItemViewModel(getMediaId(sectionState).asString(), mediaSessionConnection) {

    companion object {
        fun getMediaId(sectionState: SectionState): MediaID =
                MediaID(when (sectionState) {
                    SectionState.QUEUE -> TYPE_QUEUE
                    SectionState.INBOX -> TYPE_INBOX
                    SectionState.FAVORITE -> TYPE_FAVORITE
                    SectionState.HISTORY -> TYPE_HISTORY
                    else -> TYPE_GENRE
                })
    }

    fun refresh() { //datasourceFactory.invalidate()
    }


    fun applyFilter(feedUrl: String?) {
        //sourceFactory.applyFilter(feedUrl)
        //episodes.value?.dataSource?.invalidate()
    }
}