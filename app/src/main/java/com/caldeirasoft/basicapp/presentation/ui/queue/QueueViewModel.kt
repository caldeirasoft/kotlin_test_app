package com.caldeirasoft.basicapp.presentation.ui.queue

import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.basicapp.presentation.ui.episodelist.EpisodeListViewModel
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.MediaID

class QueueViewModel(mediaSessionConnection: MediaSessionConnection,
                     val episodeRepository: EpisodeRepository)
    : ViewModel() {

    val dataItems: LiveData<List<MediaSessionCompat.QueueItem>>
            = mediaSessionConnection.queueList

    init {
        // init media browser
    }
}