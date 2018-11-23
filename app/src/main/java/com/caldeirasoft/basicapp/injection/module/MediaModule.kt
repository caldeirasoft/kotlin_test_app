package com.caldeirasoft.basicapp.injection.module

import android.content.Context
import com.caldeirasoft.basicapp.api.feedly.retrofit.FeedlyAPI
import com.caldeirasoft.basicapp.api.itunes.retrofit.ITunesAPI
import com.caldeirasoft.basicapp.data.db.AppDatabase
import com.caldeirasoft.basicapp.data.db.episodes.EpisodeDao
import com.caldeirasoft.basicapp.data.db.podcasts.PodcastDao
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.service.mediaplayback.QueueManager
import com.github.salomonbrys.kodein.*

class MediaModule
{
    /*
    val bind = Kodein.Module {
        bind<QueueManager>() with singleton { QueueManager() }
    }
    */
}
