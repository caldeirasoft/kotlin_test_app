package com.caldeirasoft.castly.domain.usecase

import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.repository.FeedlyRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class SubscribeToPodcastUseCase(val feedlyRepository: FeedlyRepository,
                                val podcastRepository: PodcastRepository,
                                val episodeRepository: EpisodeRepository)
    : BaseUseCase<SubscribeToPodcastUseCase.Params, Boolean>() {

    override suspend fun run(params: Params): Deferred<Boolean> = GlobalScope.async {
        // get podcast info
        feedlyRepository
                .getPodcastFromFeedlyApi(params.podcast.feedUrl)
                .await()
                ?.let {
                    params.podcast.description = it.description
                    params.podcast.updated = it.updated
                    podcastRepository.insert(params.podcast).await()
                }

        // get last episode
        feedlyRepository
                .getLastEpisode(params.podcast)
                .await()
                ?.apply {
                    this.section = SectionState.INBOX.value
                    episodeRepository.insert(this).await()
                }

        true
    }

    data class Params(val podcast:Podcast)
}