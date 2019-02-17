package com.caldeirasoft.castly.domain.usecase

import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.repository.FeedlyRepository
import com.caldeirasoft.castly.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.Deferred

class GetPodcastFromFeedlyUseCase(val feedlyRepository: FeedlyRepository)
    : BaseUseCase<GetPodcastFromFeedlyUseCase.Params, Podcast?>() {

    override suspend fun run(params: Params): Deferred<Podcast?> =
        feedlyRepository.getPodcastFromFeedlyApi(params.url)

    data class Params(val url:String)
}