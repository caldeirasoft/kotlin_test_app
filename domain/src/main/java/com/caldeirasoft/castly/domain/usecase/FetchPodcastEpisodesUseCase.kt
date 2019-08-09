package com.caldeirasoft.castly.domain.usecase

import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.caldeirasoft.castly.domain.const.Constants
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.usecase.base.UseCaseResult

class FetchPodcastEpisodesUseCase(val episodeRepository: EpisodeRepository) {
    //private val provider = PagedListProviderImpl(ItunesPodcastDataSource.factory(itunesRepository, podcastRepository, DEFAULT_CATEGORY, networkState))
    //val topItems: LiveData<PagedList<Podcast>>
        //get() = provider.provide()
    lateinit var factory: DataSource.Factory<Int, Episode>

    fun fetchAll(podcastId: Long): UseCaseResult<PagedList<Episode>> {
        factory = episodeRepository.fetchFactory(podcastId)
        val pagedList = factory.toLiveData(Constants.PAGE_LOAD_SIZE)
        return UseCaseResult(data = pagedList)
    }
}