package com.caldeirasoft.basicapp.domain.usecase

import com.caldeirasoft.basicapp.domain.entity.ItunesStore
import com.caldeirasoft.basicapp.domain.repository.ItunesRepository
import com.caldeirasoft.basicapp.domain.repository.PodcastRepository
import com.caldeirasoft.basicapp.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class GetItunesStoreUseCase(val podcastRepository: PodcastRepository,
                            val itunesRepository: ItunesRepository)
    : BaseUseCase<String, ItunesStore>() {

    override suspend fun run(params: String): Deferred<ItunesStore> = GlobalScope.async {
        val store = itunesRepository.getStore("143442-3,31").await()
        store
    }
}