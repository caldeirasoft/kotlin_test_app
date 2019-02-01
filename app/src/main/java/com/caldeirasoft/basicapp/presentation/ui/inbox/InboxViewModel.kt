package com.caldeirasoft.basicapp.presentation.ui.inbox

import com.caldeirasoft.basicapp.domain.entity.SectionState
import com.caldeirasoft.basicapp.domain.repository.EpisodeRepository
import com.caldeirasoft.basicapp.presentation.ui.episodelist.EpisodeListViewModel

class InboxViewModel(episodeRepository: EpisodeRepository)
    : EpisodeListViewModel(SectionState.INBOX, episodeRepository) {

}