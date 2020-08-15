package com.caldeirasoft.castly.domain.usecase

import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.base.BaseUseCase
import com.caldeirasoft.castly.domain.usecase.base.UseCaseResult
import com.caldeirasoft.castly.domain.util.Resource
import kotlinx.coroutines.flow.Flow

class GetPodcastUseCase(val podcastRepository: PodcastRepository)
    : BaseUseCase<Long, Resource<Podcast>>() {
    override fun execute(podcastId: Long): Flow<Resource<Podcast>> {
        return podcastRepository.getPodcast(podcastId)
    }
}