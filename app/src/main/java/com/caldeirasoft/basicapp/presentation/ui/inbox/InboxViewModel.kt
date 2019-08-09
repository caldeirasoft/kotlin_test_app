package com.caldeirasoft.basicapp.presentation.ui.inbox

import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.basicapp.presentation.ui.episodelist.EpisodeListViewModel
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.usecase.FetchSectionEpisodesUseCase

class InboxViewModel(mediaSessionConnection: MediaSessionConnection,
                     fetchSectionEpisodesUseCase: FetchSectionEpisodesUseCase)
    : EpisodeListViewModel(SectionState.INBOX, fetchSectionEpisodesUseCase) {

    init {
    }
}