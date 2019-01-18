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
import org.jetbrains.anko.doAsync
import java.util.ArrayList
import java.util.concurrent.Executor

/**
 * Created by Edmond on 15/02/2018.
 */
class EpisodeFeedlyDataProvider()
{
    val backingStoreEpisodes= arrayListOf<Episode>()
    var backingStoreContinuation = ""

    fun clear()
    {
        backingStoreEpisodes.clear()
        backingStoreContinuation = ""
    }
}