package com.caldeirasoft.castly.domain.usecase

import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.base.UseCaseResult

class FetchPodcastsInDbUseCase(val podcastRepository: PodcastRepository) {
    fun fetchAll(): UseCaseResult<List<Podcast>> {
        val podcasts = podcastRepository.fetch()
        return UseCaseResult(data = podcasts)
    }
}