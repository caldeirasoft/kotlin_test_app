package com.caldeirasoft.basicapp.presentation.ui.podcastinfo

import androidx.lifecycle.*
import com.caldeirasoft.basicapp.presentation.utils.Tuple4
import com.caldeirasoft.basicapp.presentation.utils.extensions.combineLatest
import com.caldeirasoft.basicapp.util.Event
import com.caldeirasoft.castly.domain.model.entities.Episode
import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.domain.model.entities.SectionWithCount
import com.caldeirasoft.castly.domain.usecase.*
import com.caldeirasoft.castly.domain.util.Resource
import com.caldeirasoft.castly.domain.util.Status
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
@FlowPreview
class PodcastInfoViewModel(
        val podcast: Podcast,
        private val getPodcastUseCase: GetPodcastUseCase,
        private val fetchPodcastEpisodesUseCase: FetchPodcastEpisodesUseCase,
        private val fetchEpisodeCountBySectionUseCase: FetchEpisodeCountBySectionUseCase,
        private val subscribeToPodcastUseCase: SubscribeToPodcastUseCase,
        private val unsubscribeFromPodcastUseCase: UnsubscribeFromPodcastUseCase)
    : ViewModel() {

    // podcast resource
    val podcastResourceData: Flow<Resource<Podcast>> by lazy {
        getPodcastUseCase
                .execute(podcast.id)
                .onStart { emit(Resource.loading(podcast)) }
                .flowOn(Dispatchers.IO)
    }

    // podcast from db
    val podcastData: LiveData<Podcast> =
            podcastResourceData
                    .map { it.data }
                    .filterNotNull()
                    .distinctUntilChanged()
                    .asLiveData()

    // episodes items
    var podcastEpisodes: LiveData<List<Episode>> =
            fetchPodcastEpisodesUseCase.execute(podcast.id)
                    .distinctUntilChanged()
                    .asLiveData(Dispatchers.IO)

    // loading status
    val loadingStatus: LiveData<Status> =
            podcastResourceData
                    .map { it.status }
                    .filterNotNull()
                    .distinctUntilChanged()
                    .asLiveData()

    // count by section
    val sectionCount: LiveData<SectionWithCount> =
            fetchEpisodeCountBySectionUseCase.execute(podcast.id)
                    .asLiveData(Dispatchers.IO)

    init {
        viewModelScope.launch {
            //fetchPodcastData()
        }
    }

    fun refresh() {
        val t: Flow<Episode>
    }

    fun toggleSubscription() {
        subscribeToPodcastUseCase.subscribe(podcast.id)
    }

    fun subscribeToPodcast() {
        subscribeToPodcastUseCase.subscribe(podcast.id)
    }

    fun unsubscribeFromPodcast() {
        unsubscribeFromPodcastUseCase.unsubscribe(podcast.id)
    }
}
