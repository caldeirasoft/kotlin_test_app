package com.caldeirasoft.castly.domain.usecase

import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.caldeirasoft.castly.domain.const.Constants
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.usecase.base.UseCaseResult
import java.util.concurrent.Executor

class FetchSectionEpisodesUseCase(
        val episodeRepository: EpisodeRepository) {
    fun fetchAll(sectionId: Int): UseCaseResult<PagedList<Episode>> {
        val factory = episodeRepository.fetchFactory(sectionId)
        val pagedList = factory.toLiveData(
                pageSize = Constants.PAGE_LOAD_SIZE)
        return UseCaseResult(data = pagedList)
    }
}