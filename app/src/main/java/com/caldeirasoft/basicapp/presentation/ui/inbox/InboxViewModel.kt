package com.caldeirasoft.basicapp.presentation.ui.inbox

import com.caldeirasoft.basicapp.presentation.ui.episodelist.EpisodeListViewModel
import com.caldeirasoft.castly.domain.model.entities.SectionState
import com.caldeirasoft.castly.domain.usecase.FetchEpisodeCountByPodcastUseCase
import com.caldeirasoft.castly.domain.usecase.FetchSectionEpisodesUseCase

class InboxViewModel(fetchSectionEpisodesUseCase: FetchSectionEpisodesUseCase,
                     fetchEpisodeCountByPodcastUseCase: FetchEpisodeCountByPodcastUseCase)
    : EpisodeListViewModel(SectionState.ALL_EPISODES, fetchSectionEpisodesUseCase, fetchEpisodeCountByPodcastUseCase) {
    //SectionState.INBOX

    init {
    }
}