package com.caldeirasoft.basicapp.presentation.ui.podcastinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.NetworkState
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.model.SectionWithCount
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.*
import com.caldeirasoft.castly.domain.usecase.base.UseCaseResult

class PodcastInfoViewModel(
        val podcastId: Long,
        val podcast: Podcast?,
        val fetchPodcastEpisodesUseCase: FetchPodcastEpisodesUseCase,
        val getPodcastInDbUseCase: GetPodcastInDbUseCase,
        val fetchEpisodeCountBySectionUseCase: FetchEpisodeCountBySectionUseCase,
        val subscribeToPodcastUseCase: SubscribeToPodcastUseCase,
        val updatePodcastFromItunesUseCase: UpdatePodcastFromItunesUseCase)
    : ViewModel() {

    // podcast from arg
    var podcastFromArg: LiveData<Podcast> = MutableLiveData<Podcast>().apply {
        postValue(podcast)
    }

    // usecase result
    private val getPodcastInDbUseCaseResult: UseCaseResult<Podcast> by lazy { getPodcastInDbUseCase.get(podcastId) }
    // podcast from db
    val podcastDb: LiveData<Podcast> by lazy { getPodcastInDbUseCaseResult.data }

    // podcastData
    val podcastLiveData: MediatorLiveData<Podcast> = MediatorLiveData<Podcast>().apply {
        addSource(podcastFromArg) { podcast -> postValue(podcast) }
        addSource(podcastDb) { podcast -> podcast?.let { postValue(it) } }
    }

    // data items
    var dataItems: LiveData<PagedList<Episode>>

    // loading state
    var initialState: LiveData<NetworkState>

    // loading state
    var networkState: LiveData<NetworkState>

    // count by section
    val sectionCount: LiveData<SectionWithCount>

    //endregion

    init {
        // fetch episodes from Db
        fetchPodcastEpisodesUseCase.fetchAll(podcastId).let {
            dataItems = it.data
            initialState = it.initialState
            networkState = it.networkState
        }

        // fetch episodes count by section
        fetchEpisodeCountBySectionUseCase.fetchAll(podcastId).let {
            sectionCount = it.data
        }

        // update podcasts from itunes
        updatePodcastFromItunesUseCase.updatePodcast(podcastId)
    }

    fun refresh() {
        dataItems.value?.dataSource?.invalidate()
    }


    fun toggleSubscription() {
        subscribeToPodcastUseCase.subscribe(podcastId)
    }

    fun subscribeToPodcast() {
        subscribeToPodcastUseCase.subscribe(podcastId)
    }
}
