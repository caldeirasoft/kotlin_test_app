package com.caldeirasoft.basicapp.domain.usecase

import androidx.lifecycle.LiveData
import com.caldeirasoft.basicapp.domain.entity.Episode
import com.caldeirasoft.basicapp.domain.entity.Podcast
import com.caldeirasoft.basicapp.domain.repository.FeedlyRepository
import com.caldeirasoft.basicapp.domain.repository.PodcastRepository
import com.caldeirasoft.basicapp.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class GetPodcastFromFeedlyUseCase(val feedlyRepository: FeedlyRepository)
    : BaseUseCase<GetPodcastFromFeedlyUseCase.Params, Podcast?>() {

    override suspend fun run(params: Params): Deferred<Podcast?> =
        feedlyRepository.getPodcastFromFeedlyApi(params.url)

    data class Params(val url:String)
}