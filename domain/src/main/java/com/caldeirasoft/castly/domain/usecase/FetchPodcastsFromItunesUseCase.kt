package com.caldeirasoft.castly.domain.usecase

import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.caldeirasoft.castly.domain.const.Constants
import com.caldeirasoft.castly.domain.datasource.ItunesPodcastDataSource
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.repository.ItunesRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.base.UseCaseResult

class FetchPodcastsFromItunesUseCase(
        val itunesRepository: ItunesRepository,
        val podcastRepository: PodcastRepository) {

    fun fetchAll(genreId: Int): UseCaseResult<PagedList<Podcast>> {
        val sourceFactory = ItunesPodcastDataSource.Factory(itunesRepository, podcastRepository, genreId)
        val livePagedList = sourceFactory.toLiveData(Constants.PAGE_LOAD_SIZE)
        val initialState = Transformations.switchMap(sourceFactory.sourceLiveData) { it.networkState }
        val networkState = Transformations.switchMap(sourceFactory.sourceLiveData) { it.networkState }
        return UseCaseResult(
                data = livePagedList,
                initialState = initialState,
                networkState = networkState)
    }
}