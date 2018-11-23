package com.caldeirasoft.basicapp.ui.episodedetail

import androidx.lifecycle.ViewModel
import com.caldeirasoft.basicapp.data.db.episodes.EpisodeDataSource
import com.caldeirasoft.basicapp.data.db.podcasts.PodcastDataSource
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.repository.EpisodeRepository
import com.caldeirasoft.basicapp.data.repository.PodcastRepository
import com.caldeirasoft.basicapp.ui.base.SingleLiveEvent
import com.caldeirasoft.basicapp.ui.catalog.CatalogViewModel
import org.jetbrains.anko.doAsync
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class EpisodeDetailViewModel : ViewModel() {

    var episodeRepository: EpisodeDataSource
    lateinit private var mainThreadExecutor: Executor
    private var ioExecutor: Executor

    internal val playEpisodeEvent = SingleLiveEvent<Episode>()
    internal val updateEpisodeEvent = SingleLiveEvent<Episode>()

    init {
        episodeRepository = EpisodeRepository()
        ioExecutor = Executors.newFixedThreadPool(5)
    }

    companion object {
        val PAGE_SIZE = 15
    }

    fun setEpisode(itemId:String)
    {
        doAsync {
            //episode = episodeRepository.getEpisodeById(itemId)!!
        }
    }

    fun playEpisodeDetail(episode:Episode)
    {
        playEpisodeEvent.value = episode
    }
}