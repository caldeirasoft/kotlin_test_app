package com.caldeirasoft.castly.domain.usecase

import com.caldeirasoft.castly.domain.model.entities.SectionWithCount
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.usecase.base.BaseUseCase
import com.caldeirasoft.castly.domain.usecase.base.UseCaseResult
import kotlinx.coroutines.flow.Flow

class FetchEpisodeCountBySectionUseCase(val episodeRepository: EpisodeRepository)
    : BaseUseCase<Long, SectionWithCount>() {

    override fun execute(params: Long): Flow<SectionWithCount> =
            episodeRepository.fetchEpisodeCountBySection(params)

}