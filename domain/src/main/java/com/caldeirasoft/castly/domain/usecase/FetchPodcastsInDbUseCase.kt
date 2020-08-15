package com.caldeirasoft.castly.domain.usecase

import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.base.UseCaseResult

class FetchPodcastsInDbUseCase(val podcastRepository: PodcastRepository) {
    fun fetchAll(): UseCaseResult<List<Podcast>> {
        val podcasts = podcastRepository.fetchSubscribedPodcasts()
        return UseCaseResult(data = podcasts)
    }
}