package com.caldeirasoft.basicapp.presentation.ui.podcast

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.basicapp.presentation.ui.base.MediaItemViewModel
import com.caldeirasoft.castly.domain.model.MediaID
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import org.koin.core.parameter.parametersOf

class PodcastViewModel(val podcastRepository: PodcastRepository ,
                       mediaSessionConnection: MediaSessionConnection)
    : MediaItemViewModel<Podcast>(MediaID(SectionState.ALL_PODCASTS).asString(), mediaSessionConnection)
{
    override val dataItems: LiveData<List<Podcast>>
            = podcastRepository.fetch()
}