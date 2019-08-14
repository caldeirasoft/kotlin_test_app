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
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.FetchPodcastEpisodesUseCase
import com.caldeirasoft.castly.domain.usecase.GetPodcastInDbUseCase
import com.caldeirasoft.castly.domain.usecase.SubscribeToPodcastUseCase
import com.caldeirasoft.castly.domain.usecase.UpdatePodcastFromItunesUseCase
import com.caldeirasoft.castly.domain.usecase.base.UseCaseResult

class PodcastInfoViewModel(
        val podcastId: Long,
        val podcast: Podcast?,
        val podcastRepository: PodcastRepository,
        val episodeRepository: EpisodeRepository,
        val mediaSessionConnection: MediaSessionConnection,
        val fetchPodcastEpisodesUseCase: FetchPodcastEpisodesUseCase,
        val getPodcastInDbUseCase: GetPodcastInDbUseCase,
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

    //endregion

    init {
        fetchPodcastEpisodesUseCase.fetchAll(podcastId).let {
            dataItems = it.data
            initialState = it.initialState
            networkState = it.networkState
        }

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
