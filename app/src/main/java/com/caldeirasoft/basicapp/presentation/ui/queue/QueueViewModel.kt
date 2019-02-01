package com.caldeirasoft.basicapp.presentation.ui.queue

import com.caldeirasoft.basicapp.domain.entity.SectionState
import com.caldeirasoft.basicapp.domain.repository.EpisodeRepository
import com.caldeirasoft.basicapp.presentation.ui.episodelist.EpisodeListViewModel

class QueueViewModel(episodeRepository: EpisodeRepository)
    : EpisodeListViewModel(SectionState.QUEUE, episodeRepository) {

}