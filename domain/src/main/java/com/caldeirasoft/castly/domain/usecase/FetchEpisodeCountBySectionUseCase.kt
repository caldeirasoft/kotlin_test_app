package com.caldeirasoft.castly.domain.usecase

import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.caldeirasoft.castly.domain.const.Constants
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.PodcastWithCount
import com.caldeirasoft.castly.domain.model.SectionWithCount
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.usecase.base.UseCaseResult
import java.util.concurrent.Executor

class FetchEpisodeCountBySectionUseCase(val episodeRepository: EpisodeRepository) {
    fun fetchAll(podcastId: Long): UseCaseResult<SectionWithCount> {
        val episodeCount = episodeRepository.fetchEpisodeCountBySection(podcastId)
        return UseCaseResult(data = episodeCount)
    }
}