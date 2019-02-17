package com.caldeirasoft.castly.domain.usecase

import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class ArchiveEpisodeUseCase(val episodeRepository: EpisodeRepository)
    : BaseUseCase<ArchiveEpisodeUseCase.Params, Boolean>() {

    override suspend fun run(params: Params): Deferred<Boolean> = GlobalScope.async {
        episodeRepository.getSync(params.id)?.let { episode ->
            val lastSection = episode.section
            episode.section = SectionState.ARCHIVE.value
            episodeRepository.update(episode).await()

            if (lastSection == SectionState.QUEUE.value) {
                // move all episodes from queue down 1 position
                episodeRepository.fetchQueueSync().let {
                    it.forEachIndexed { i, episode -> episode.queuePosition = i }
                    episodeRepository.update(it).await()
                }
            }
        }
        true
    }

    data class Params(val id:String)
}