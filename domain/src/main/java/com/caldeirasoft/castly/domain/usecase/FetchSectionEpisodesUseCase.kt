package com.caldeirasoft.castly.domain.usecase

import com.caldeirasoft.castly.domain.model.entities.Episode
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.flow.Flow

class FetchSectionEpisodesUseCase(val episodeRepository: EpisodeRepository)
    : BaseUseCase<Int, List<Episode>>() {
    override fun execute(params: Int): Flow<List<Episode>> =
            episodeRepository.fetch(params)
}