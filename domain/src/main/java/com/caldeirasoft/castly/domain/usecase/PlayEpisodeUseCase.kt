package com.caldeirasoft.castly.domain.usecase

import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class PlayEpisodeUseCase(val episodeRepository: EpisodeRepository)
    : BaseUseCase<PlayEpisodeUseCase.Params, Boolean>() {

    override suspend fun run(params: Params): Deferred<Boolean> = GlobalScope.async {
        params.episode.let { episode ->

            episodeRepository.fetchQueueSync().let { list ->
                episode.section = SectionState.QUEUE.value
                when (list.isEmpty()) {
                    true -> {
                        // queue episode first
                        episode.queuePosition = 1
                        // play episode
                        //playEpisode(episode)
                    }
                    else -> {
                        // move episodes from queue
                        list.forEachIndexed { i, episode -> if (i > 0) episode.queuePosition = i + 1 }
                        episodeRepository.update(list).await()
                        // queue episode
                        episode.queuePosition = 1
                    }
                }
                episodeRepository.update(episode).await()
            }
        }

        true
    }

    data class Params(val episode: Episode)
}