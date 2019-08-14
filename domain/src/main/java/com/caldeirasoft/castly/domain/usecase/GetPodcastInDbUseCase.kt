package com.caldeirasoft.castly.domain.usecase

import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.base.UseCaseResult

class GetPodcastInDbUseCase(val podcastRepository: PodcastRepository) {
    fun get(podcastId: Long): UseCaseResult<Podcast> {
        val podcast = podcastRepository.get(podcastId)
        return UseCaseResult(data = podcast)
    }
}