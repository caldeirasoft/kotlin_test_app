package com.caldeirasoft.castly.domain.usecase

import com.caldeirasoft.castly.domain.model.entities.Episode
import com.caldeirasoft.castly.domain.model.entities.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.usecase.base.BaseDeferredUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QueueEpisodeUseCase(val episodeRepository: EpisodeRepository)
    : BaseDeferredUseCase<QueueEpisodeUseCase.Params, Boolean>() {

    override suspend fun run(params: Params): Boolean =// move episodes from queue
    // queue episode
            // play episode
            withContext(Dispatchers.Default) {
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
                                        //episodeRepository.update(list)
                                        // queue episode
                                        episode.queuePosition = 1
                                    }
                                    else -> {
                                        episode.queuePosition = list.size + 1
                                    }
                                }
                            }
                        }
                        //episodeRepository.upsert(episode)
                    }
                }

                true
            }

    data class Params(val episode: Episode, val position: QueuePosition)

    enum class QueuePosition {
        First, Last
    }
}