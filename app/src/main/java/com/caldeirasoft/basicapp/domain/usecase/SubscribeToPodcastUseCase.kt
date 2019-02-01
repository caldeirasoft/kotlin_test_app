package com.caldeirasoft.basicapp.domain.usecase

import androidx.lifecycle.LiveData
import com.caldeirasoft.basicapp.domain.entity.Episode
import com.caldeirasoft.basicapp.domain.entity.Podcast
import com.caldeirasoft.basicapp.domain.entity.SectionState
import com.caldeirasoft.basicapp.domain.repository.EpisodeRepository
import com.caldeirasoft.basicapp.domain.repository.FeedlyRepository
import com.caldeirasoft.basicapp.domain.repository.PodcastRepository
import com.caldeirasoft.basicapp.domain.usecase.base.BaseUseCase
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
                    podcastRepository.insertPodcast(params.podcast)
                }

        // get last episode
        feedlyRepository
                .getLastEpisode(params.podcast)
                .await()
                ?.apply {
                    this.section = SectionState.INBOX.value
                    episodeRepository.insertEpisode(this)
                }

        true
    }

    data class Params(val podcast:Podcast)
}