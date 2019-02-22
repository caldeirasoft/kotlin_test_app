package com.caldeirasoft.basicapp.presentation.ui.inbox

import androidx.lifecycle.LiveData
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.basicapp.presentation.ui.episodelist.EpisodeListViewModel
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.MediaID

class InboxViewModel(mediaSessionConnection: MediaSessionConnection,
                     val episodeRepository: EpisodeRepository)
    : EpisodeListViewModel(SectionState.INBOX, mediaSessionConnection, episodeRepository) {

    override val dataItems: LiveData<List<Episode>>
            = episodeRepository.fetch(SectionState.INBOX.value)
}