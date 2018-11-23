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
class EpisodeInboxDataSourceFactory(
        val episodeDao: EpisodeDao,
        private var filter: String?
) : DataSource.Factory<Int, Episode>()
{
    override fun create(): DataSource<Int, Episode> {
        return when (filter.isNullOrBlank()) {
            true -> episodeDao.fetchEpisodesBySection(SectionState.INBOX.value).create()
            else -> episodeDao.fetchEpisodesBySection(SectionState.INBOX.value, filter!!).create()
        }
    }

    fun applyFilter(filter: String?)
    {
        val newFilter:String? = if (filter.isNullOrBlank()) { null } else { filter }
        this.filter = newFilter
    }
}