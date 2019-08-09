package com.caldeirasoft.basicapp.presentation.ui.queue

import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.LiveData
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.basicapp.presentation.ui.base.BaseViewModel
import com.caldeirasoft.castly.domain.repository.EpisodeRepository

class QueueViewModel(mediaSessionConnection: MediaSessionConnection,
                     val episodeRepository: EpisodeRepository)
    : BaseViewModel() {

    val dataItems: LiveData<List<MediaSessionCompat.QueueItem>>
            = mediaSessionConnection.queueList

    init {
        // init media browser
    }
}