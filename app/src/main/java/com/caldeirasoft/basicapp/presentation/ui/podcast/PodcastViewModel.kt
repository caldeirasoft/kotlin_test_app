package com.caldeirasoft.basicapp.presentation.ui.podcast

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.basicapp.presentation.ui.base.ListItemViewModel
import com.caldeirasoft.castly.domain.model.entities.MediaID
import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.domain.model.entities.SectionState
import com.caldeirasoft.castly.domain.repository.PodcastRepository

class PodcastViewModel(val podcastRepository: PodcastRepository ,
                       mediaSessionConnection: MediaSessionConnection)
    : ListItemViewModel<Podcast>(MediaID(SectionState.ALL_PODCASTS).asString()) {
    override val dataItems: LiveData<List<Podcast>> =
            podcastRepository.fetchSubscribedPodcasts()
                    .asLiveData()
}