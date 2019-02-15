package com.caldeirasoft.castly.domain.usecase

import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class UnsubscribeUseCase(val podcastRepository: PodcastRepository,
                         val episodeRepository: EpisodeRepository)
    : BaseUseCase<UnsubscribeUseCase.Params, Boolean>() {

    override suspend fun run(params: Params): Deferred<Boolean> = GlobalScope.async {
        // delete podcast
        podcastRepository.delete(params.podcast).await()

        // delete episodes
        episodeRepository.deleteByPodcast(params.podcast.feedUrl).await()

        true
    }

    data class Params(val podcast:Podcast)
}