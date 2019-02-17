package com.caldeirasoft.basicapp.presentation.ui.queue

import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.basicapp.presentation.ui.episodelist.EpisodeListViewModel
import com.caldeirasoft.castly.domain.model.MediaID

class QueueViewModel(mediaSessionConnection: MediaSessionConnection,
                     episodeRepository: EpisodeRepository)
    : EpisodeListViewModel(SectionState.QUEUE, mediaSessionConnection, episodeRepository) {

}