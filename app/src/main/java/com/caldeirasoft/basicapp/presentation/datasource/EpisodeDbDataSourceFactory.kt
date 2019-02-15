package com.caldeirasoft.basicapp.presentation.datasource

import androidx.paging.*
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.repository.EpisodeRepository

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
            true -> episodeRepository.fetchFactory(section).create()
            else -> episodeRepository.fetchFactory(section, filter!!).create()
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