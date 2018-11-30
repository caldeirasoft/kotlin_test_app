package com.caldeirasoft.basicapp.ui.queue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import com.caldeirasoft.basicapp.Mockup
import com.caldeirasoft.basicapp.data.db.episodes.EpisodeDataSource
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.repository.EpisodeRepository
import com.caldeirasoft.basicapp.ui.common.SingleLiveEvent
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
        //TODO: remove : episodes = episodeRepository.getEpisodesBySection(SectionState.QUEUE.value)
        val mutableEpisodes = MutableLiveData<List<Episode>>()
        val listEpisodes = Mockup.getEpisodesMockup()?.let {
            it.sortedBy { episode -> episode.title }.let {
                mutableEpisodes.postValue(it)
            }
        }
        episodes = mutableEpisodes
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