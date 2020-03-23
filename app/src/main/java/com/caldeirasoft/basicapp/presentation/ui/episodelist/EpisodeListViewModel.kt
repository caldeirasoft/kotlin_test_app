package com.caldeirasoft.basicapp.presentation.ui.episodelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.caldeirasoft.basicapp.presentation.ui.base.BaseViewModel
import com.caldeirasoft.castly.domain.model.*
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.FetchEpisodeCountByPodcastUseCase
import com.caldeirasoft.castly.domain.usecase.FetchEpisodeCountBySectionUseCase
import com.caldeirasoft.castly.domain.usecase.FetchSectionEpisodesUseCase

abstract class EpisodeListViewModel(
        sectionState: SectionState,
        fetchSectionEpisodesUseCase: FetchSectionEpisodesUseCase,
        fetchEpisodeCountByPodcastUseCase: FetchEpisodeCountByPodcastUseCase)
    : BaseViewModel() {

    // podcast with count
    var podcastsWithCount: LiveData<List<PodcastWithCount>>

    // data items
    var dataItems: LiveData<PagedList<Episode>>

    // dark theme
    val darkThemeOn: MutableLiveData<Boolean> = MutableLiveData()
    /*private val mediator = MediatorLiveData<Boolean>().apply {
        addSource(darkThemeOn) { value ->
            setValue(value)
        }
    }.also { it.observeForever {  } }*/

    init {
        fetchSectionEpisodesUseCase.fetchAll(sectionState.value).let {
            dataItems = it.data
        }

        fetchEpisodeCountByPodcastUseCase.fetchAll(sectionState.value).let {
            podcastsWithCount = it.data
        }
    }

    fun applyFilter(feedUrl: String?) {
        //sourceFactory.applyFilter(feedUrl)
        //episodes.value?.dataSource?.invalidate()
    }
}