package com.caldeirasoft.castly.domain.usecase

import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.repository.FeedlyRepository
import com.caldeirasoft.castly.domain.usecase.base.BaseDeferredUseCase
import com.caldeirasoft.castly.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class GetPodcastFromFeedlyUseCase(val feedlyRepository: FeedlyRepository)
    : BaseDeferredUseCase<GetPodcastFromFeedlyUseCase.Params, Podcast?>() {

    override suspend fun run(params: Params): Deferred<Podcast?> =
            GlobalScope.async {
                feedlyRepository.getPodcastFromFeedlyApi(params.url)
            }

    data class Params(val url:String)
}