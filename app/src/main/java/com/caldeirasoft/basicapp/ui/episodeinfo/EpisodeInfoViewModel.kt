package com.caldeirasoft.basicapp.ui.episodeinfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caldeirasoft.basicapp.data.db.episodes.EpisodeDataSource
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.repository.EpisodeRepository
import com.caldeirasoft.basicapp.ui.common.SingleLiveEvent
import org.jetbrains.anko.doAsync
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class EpisodeInfoViewModel : ViewModel() {

    var episodeRepository: EpisodeDataSource
    var episodeData = MutableLiveData<Episode>()

    internal val playEpisodeEvent = SingleLiveEvent<Episode>()
    internal val updateEpisodeEvent = SingleLiveEvent<Episode>()

    init {
        episodeRepository = EpisodeRepository()
    }

    companion object {
        val PAGE_SIZE = 15
    }

    fun setEpisode(itemId:String) {
        doAsync {
            //episode = episodeRepository.getEpisodeById(itemId)!!
        }
    }

    fun setEpisode(episode:Episode) {
        episodeData.postValue(episode)
    }



    fun playEpisodeDetail(episode:Episode)
    {
        playEpisodeEvent.value = episode
    }
}