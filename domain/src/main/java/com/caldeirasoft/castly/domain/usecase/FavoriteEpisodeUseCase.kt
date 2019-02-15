package com.caldeirasoft.castly.domain.usecase

import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class FavoriteEpisodeUseCase(val episodeRepository: EpisodeRepository)
    : BaseUseCase<FavoriteEpisodeUseCase.Params, Boolean>() {

    override suspend fun run(params: Params): Deferred<Boolean> = GlobalScope.async {
        episodeRepository.getSync(params.id)?.let { episode ->
            episode.isFavorite = params.isFavorite
            episodeRepository.update(episode).await()
        }
        true
    }

    data class Params(val id:String, val isFavorite:Boolean)
}