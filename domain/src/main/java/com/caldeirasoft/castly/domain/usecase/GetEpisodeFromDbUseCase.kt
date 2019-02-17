package com.caldeirasoft.castly.domain.usecase

import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class GetEpisodeFromDbUseCase(val episodeRepository: EpisodeRepository)
    : BaseUseCase<GetEpisodeFromDbUseCase.Params, Episode?>() {

    override suspend fun run(params: Params): Deferred<Episode?> = GlobalScope.async {
        episodeRepository.getSync(params.id)
    }

    data class Params(val id:String)
}