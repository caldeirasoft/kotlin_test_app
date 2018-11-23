package com.caldeirasoft.basicapp.ui.queue

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import com.caldeirasoft.basicapp.data.db.episodes.EpisodeDataSource
import com.caldeirasoft.basicapp.data.db.podcasts.PodcastDataSource
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.enum.SectionState
import com.caldeirasoft.basicapp.data.repository.EpisodeRepository
import com.caldeirasoft.basicapp.data.repository.PodcastRepository
import com.caldeirasoft.basicapp.ui.base.SingleLiveEvent
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class QueueViewModel : ViewModel() {

    lateinit var episodeRepository: EpisodeDataSource
    lateinit private var mainThreadExecutor: Executor
    lateinit private var ioExecutor: Executor

    lateinit var episodes: LiveData<List<Episode>>
    lateinit var datasourceFactory: DataSource.Factory<Int, Podcast>

    internal val openPodcastEvent = SingleLiveEvent<Podcast>()
    internal val updatePodcastEvent = SingleLiveEvent<Podcast>()

    init {
        ioExecutor = Executors.newFixedThreadPool(5)
        episodeRepository = EpisodeRepository()
        episodes = episodeRepository.getEpisodesBySection(SectionState.QUEUE.value)
    }

    companion object {
        val PAGE_SIZE = 15
    }

    fun refresh() { //datasourceFactory.invalidate()
    }

    fun onPodcastClick(podcast:Podcast)
    {
        openPodcastEvent.value = podcast
    }

}