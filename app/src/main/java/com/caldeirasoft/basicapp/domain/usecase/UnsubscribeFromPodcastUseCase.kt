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

class UnsubscribeFromPodcastUseCase(val podcastRepository: PodcastRepository,
                                    val episodeRepository: EpisodeRepository)
    : BaseUseCase<UnsubscribeFromPodcastUseCase.Params, Boolean>() {

    override suspend fun run(params: Params): Deferred<Boolean> = GlobalScope.async {
        // delete podcast
        podcastRepository.deletePodcast(params.podcast)

        // delete episodes
        episodeRepository.deleteEpisodes(params.podcast.feedUrl)

        true
    }

    data class Params(val podcast:Podcast)
}