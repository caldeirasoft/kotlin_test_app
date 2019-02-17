package com.caldeirasoft.castly.domain.usecase

import com.caldeirasoft.castly.domain.model.itunes.ItunesStore
import com.caldeirasoft.castly.domain.repository.ItunesRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.base.BaseUseCase
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