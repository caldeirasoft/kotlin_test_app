package com.caldeirasoft.basicapp.presentation.ui.inbox

import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.basicapp.presentation.ui.episodelist.EpisodeListViewModel
import com.caldeirasoft.castly.domain.model.MediaID

class InboxViewModel(mediaSessionConnection: MediaSessionConnection,
                     episodeRepository: EpisodeRepository)
    : EpisodeListViewModel(SectionState.INBOX, mediaSessionConnection, episodeRepository) {

}