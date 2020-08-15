package com.caldeirasoft.castly.domain.usecase

import com.caldeirasoft.castly.domain.util.FlowPaginationModel
import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.domain.repository.ItunesRepository
import kotlinx.coroutines.CoroutineScope

@kotlinx.coroutines.FlowPreview
@kotlinx.coroutines.ExperimentalCoroutinesApi
class FetchPodcastsFromItunesUseCase(val itunesRepository: ItunesRepository) {

    fun fetchAll(clientScope: CoroutineScope,
                 pageSize: Int,
                 genreId: Int): FlowPaginationModel<Podcast> =
            itunesRepository.getTopPodcastsData(clientScope, pageSize, genreId)

}