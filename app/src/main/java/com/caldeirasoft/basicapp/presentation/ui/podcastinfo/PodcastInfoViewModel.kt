package com.caldeirasoft.basicapp.presentation.ui.podcastinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.MediaID
import com.caldeirasoft.castly.domain.model.NetworkState
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.FetchPodcastEpisodesUseCase
import com.caldeirasoft.castly.domain.usecase.SubscribeToPodcastUseCase

class PodcastInfoViewModel(
        val mediaId: String,
        val podcast: Podcast?,
        val podcastRepository: PodcastRepository,
        val episodeRepository: EpisodeRepository,
        val mediaSessionConnection: MediaSessionConnection,
        val fetchPodcastEpisodesUseCase: FetchPodcastEpisodesUseCase,
        val subscribeToPodcastUseCase: SubscribeToPodcastUseCase)
    : ViewModel() {

    // podcastid
    val podcastId = MediaID.fromString(mediaId).id

    // podcast from db
    lateinit var podcastDb : LiveData<Podcast>

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
    }

    //hacky way to force reload items (e.g. song sort order changed)
    /*
    fun reloadMediaItems() {
        mediaSessionConnection.unsubscribe(id.asString(), subscriptionCallback)
        mediaSessionConnection.subscribe(id.asString(), subscriptionCallback)
    }
    */

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