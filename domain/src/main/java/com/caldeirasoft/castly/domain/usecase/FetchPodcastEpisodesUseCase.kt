package com.caldeirasoft.castly.domain.usecase

import com.caldeirasoft.castly.domain.model.entities.Episode
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.flow.Flow

class FetchPodcastEpisodesUseCase(val episodeRepository: EpisodeRepository)
    : BaseUseCase<Long, List<Episode>>() {

    override fun execute(podcastId: Long): Flow<List<Episode>> =
            episodeRepository.fetch(podcastId)

}