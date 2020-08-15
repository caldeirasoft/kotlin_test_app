package com.caldeirasoft.castly.domain.usecase

import com.caldeirasoft.castly.domain.model.entities.PodcastWithCount
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.usecase.base.BaseUseCase
import com.caldeirasoft.castly.domain.usecase.base.UseCaseResult
import kotlinx.coroutines.flow.Flow

class FetchEpisodeCountByPodcastUseCase(val episodeRepository: EpisodeRepository)
    : BaseUseCase<Int, List<PodcastWithCount>>() {

    override fun execute(params: Int): Flow<List<PodcastWithCount>> =
            episodeRepository.fetchEpisodesCountByPodcast(params)
}