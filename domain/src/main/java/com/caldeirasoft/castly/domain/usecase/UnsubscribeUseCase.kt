package com.caldeirasoft.castly.domain.usecase

import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.base.BaseDeferredUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UnsubscribeUseCase(val podcastRepository: PodcastRepository,
                         val episodeRepository: EpisodeRepository)
    : BaseDeferredUseCase<UnsubscribeUseCase.Params, Boolean>() {

    override suspend fun run(params: Params): Boolean = // delete podcast
            withContext(Dispatchers.Default) {
                        // delete podcast
                        podcastRepository.delete(params.podcast)

                        // delete episodes
                        episodeRepository.deleteByPodcast(params.podcast.feedUrl)

                        true
                    }

    data class Params(val podcast:Podcast)
}