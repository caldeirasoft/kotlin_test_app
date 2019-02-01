package com.caldeirasoft.basicapp.domain.usecase

import com.caldeirasoft.basicapp.domain.entity.Episode
import com.caldeirasoft.basicapp.domain.entity.ItunesStore
import com.caldeirasoft.basicapp.domain.entity.Podcast
import com.caldeirasoft.basicapp.domain.entity.SectionState
import com.caldeirasoft.basicapp.domain.repository.EpisodeRepository
import com.caldeirasoft.basicapp.domain.repository.FeedlyRepository
import com.caldeirasoft.basicapp.domain.repository.ItunesRepository
import com.caldeirasoft.basicapp.domain.repository.PodcastRepository
import com.caldeirasoft.basicapp.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class GetEpisodeFromDbUseCase(val episodeRepository: EpisodeRepository)
    : BaseUseCase<GetEpisodeFromDbUseCase.Params, Episode?>() {

    override suspend fun run(params: Params): Deferred<Episode?> = GlobalScope.async {
        episodeRepository.getEpisodeById(params.id)
    }

    data class Params(val id:String)
}