package com.caldeirasoft.basicapp.presentation.datasource

import androidx.paging.*
import com.caldeirasoft.basicapp.domain.entity.Episode
import com.caldeirasoft.basicapp.domain.repository.EpisodeRepository

/**
 * Created by Edmond on 15/02/2018.
 */
class EpisodeDbDataSourceFactory(
        var section: Int,
        var filter: String? = null,
        val episodeRepository: EpisodeRepository
        ) : DataSource.Factory<Int, Episode>()
{
    override fun create(): DataSource<Int, Episode> {
        return when (filter.isNullOrBlank()) {
            true -> episodeRepository.fetchEpisodesBySection(section).create()
            else -> episodeRepository.fetchEpisodesBySection(section, filter!!).create()
        }
    }

    fun applyFilter(filter: String?) {
        val newFilter:String? = if (filter.isNullOrBlank()) { null } else { filter }
        this.filter = newFilter
    }

    fun applySection(newSection: Int) {
        this.section = newSection
    }
}