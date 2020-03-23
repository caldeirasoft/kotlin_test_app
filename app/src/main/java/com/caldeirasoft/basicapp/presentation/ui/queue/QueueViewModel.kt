package com.caldeirasoft.basicapp.presentation.ui.queue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media2.common.MediaItem
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.basicapp.presentation.ui.base.BaseViewModel
import com.caldeirasoft.castly.domain.repository.EpisodeRepository

class QueueViewModel(mediaSessionConnection: MediaSessionConnection,
                     val episodeRepository: EpisodeRepository)
    : BaseViewModel() {

    val dataItems: LiveData<List<MediaItem>>
            = MutableLiveData()

    init {
        // init media browser
    }
}