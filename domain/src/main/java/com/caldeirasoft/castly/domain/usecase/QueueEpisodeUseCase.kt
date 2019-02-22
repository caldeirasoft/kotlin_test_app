package com.caldeirasoft.castly.domain.usecase

import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.usecase.base.BaseDeferredUseCase
import com.caldeirasoft.castly.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class QueueEpisodeUseCase(val episodeRepository: EpisodeRepository)
    : BaseDeferredUseCase<QueueEpisodeUseCase.Params, Boolean>() {

    override suspend fun run(params: Params): Deferred<Boolean> = GlobalScope.async {
        params.episode.let { episode ->
            episodeRepository.fetchQueueSync().let { list ->
                episode.section = SectionState.QUEUE.value
                when (list.isEmpty()) {
                    true -> {
                        // play episode
                        episode.queuePosition = 1
                    }
                    else -> {
                        when (params.position) {
                            QueuePosition.First -> {
                                // move episodes from queue
                                list.forEachIndexed { i, episode -> if (i > 0) episode.queuePosition = i + 1 }
                                episodeRepository.update(list)
                                // queue episode
                                episode.queuePosition = 1
                            }
                            else -> {
                                episode.queuePosition = list.size + 1
                            }
                        }
                    }
                }
                episodeRepository.upsert(episode)
            }
        }

        true
    }

    data class Params(val episode:Episode, val position: QueuePosition)

    enum class QueuePosition {
        First, Last
    }
}