package com.caldeirasoft.castly.domain.usecase

import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.repository.FeedlyRepository
import com.caldeirasoft.castly.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class GetEpisodesFromFeedlyUseCase(val feedlyRepository: FeedlyRepository,
                                   val episodeRepository: EpisodeRepository)
    : BaseUseCase<GetEpisodesFromFeedlyUseCase.Params, GetEpisodesFromFeedlyUseCase.Result>() {

    override suspend fun run(params: Params): Deferred<Result> = GlobalScope.async {
        val responseEntries = feedlyRepository.getStreamEntries(params.feed, params.pageSize, params.continuation).await()
        val result = Result(
                responseEntries.data,
                responseEntries.continuation ?: "")
        result.data.forEach { episode ->
            retrieveEpisodeDataFromDb(episode).await()
        }
        result
    }

    data class Params(val feed:Podcast, val pageSize:Int, val continuation: String = "")
    data class Result(var data:List<Episode>, var continuation: String)

    private fun retrieveEpisodeDataFromDb(episode: Episode): Deferred<Unit> {
        // get episode in db
        return GlobalScope.async {
            val episodeInDb: Episode? = episodeRepository.getSync(episode.episodeId)
            episodeInDb.apply {
                this?.apply {
                    // get value from db
                    episode.section = this.section
                    episode.isFavorite = this.isFavorite
                    episode.duration = this.duration
                    episode.playbackPosition = this.playbackPosition
                    episode.queuePosition = this.queuePosition
                }
            } ?: run {
                // if the episode is new / not in db : get from podcast
                // get value from podcast
                episode.section = SectionState.ARCHIVE.value
                //retrieveEpisodeDuration(episode)
            }
            Unit
        }
    }
}