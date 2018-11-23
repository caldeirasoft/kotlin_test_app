package com.caldeirasoft.basicapp.data.repository

import androidx.lifecycle.*
import androidx.paging.*
import com.avast.android.githubbrowser.extensions.retrofitCallback
import com.caldeirasoft.basicapp.api.feedly.retrofit.FeedlyAPI
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.api.itunes.retrofit.ITunesAPI
import com.caldeirasoft.basicapp.data.db.episodes.EpisodeDao
import com.caldeirasoft.basicapp.data.db.podcasts.PodcastDao
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.enum.SectionState
import org.jetbrains.anko.doAsync
import java.util.concurrent.Executor

/**
 * Created by Edmond on 15/02/2018.
 */
class EpisodeDbDataSourceFactory(
        val episodeDao: EpisodeDao,
        protected var section: Int,
        protected var filter: String?
) : DataSource.Factory<Int, Episode>()
{
    override fun create(): DataSource<Int, Episode> {
        return when (filter.isNullOrBlank()) {
            true -> episodeDao.fetchEpisodesBySection(section).create()
            else -> episodeDao.fetchEpisodesBySection(section, filter!!).create()
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