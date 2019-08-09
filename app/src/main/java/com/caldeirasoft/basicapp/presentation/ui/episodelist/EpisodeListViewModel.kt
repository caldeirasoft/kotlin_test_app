package com.caldeirasoft.basicapp.presentation.ui.episodelist

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.caldeirasoft.basicapp.presentation.ui.base.BaseViewModel
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.usecase.FetchSectionEpisodesUseCase

abstract class EpisodeListViewModel(
        sectionState: SectionState,
        fetchSectionEpisodesUseCase: FetchSectionEpisodesUseCase)
    : BaseViewModel() {

    // data items
    var dataItems: LiveData<PagedList<Episode>>

    init {
        fetchSectionEpisodesUseCase.fetchAll(sectionState.value).let {
            dataItems = it.data
        }
    }

    fun applyFilter(feedUrl: String?) {
        //sourceFactory.applyFilter(feedUrl)
        //episodes.value?.dataSource?.invalidate()
    }
}