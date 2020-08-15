package com.caldeirasoft.basicapp.presentation.ui.episodelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.paging.PagedList
import com.caldeirasoft.basicapp.presentation.ui.base.BaseViewModel
import com.caldeirasoft.castly.domain.model.entities.Episode
import com.caldeirasoft.castly.domain.model.entities.PodcastWithCount
import com.caldeirasoft.castly.domain.model.entities.SectionState
import com.caldeirasoft.castly.domain.usecase.FetchEpisodeCountByPodcastUseCase
import com.caldeirasoft.castly.domain.usecase.FetchSectionEpisodesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn

abstract class EpisodeListViewModel(
        sectionState: SectionState,
        fetchSectionEpisodesUseCase: FetchSectionEpisodesUseCase,
        fetchEpisodeCountByPodcastUseCase: FetchEpisodeCountByPodcastUseCase)
    : BaseViewModel() {

    // podcast with count
    var podcastsWithCount: Flow<List<PodcastWithCount>> =
            fetchEpisodeCountByPodcastUseCase.execute(sectionState.value)
                    .flowOn(Dispatchers.IO)
                    .distinctUntilChanged()

    // data items
    var podcastEpisodes: Flow<List<Episode>> =
            fetchSectionEpisodesUseCase.execute(sectionState.value)
                    .flowOn(Dispatchers.IO)
                    .distinctUntilChanged()

    // dark theme
    val darkThemeOn: MutableLiveData<Boolean> = MutableLiveData()
    /*private val mediator = MediatorLiveData<Boolean>().apply {
        addSource(darkThemeOn) { value ->
            setValue(value)
        }
    }.also { it.observeForever {  } }*/


    fun applyFilter(feedUrl: String?) {
        //sourceFactory.applyFilter(feedUrl)
        //episodes.value?.dataSource?.invalidate()
    }
}