package com.caldeirasoft.basicapp.presentation.ui.podcast

import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media2.MediaItem
import androidx.media2.MediaMetadata
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.basicapp.presentation.ui.base.MediaItemViewModel
import com.caldeirasoft.castly.domain.model.MediaID
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.service.playback.PodcastLibraryService.Companion.TYPE_ALL_PODCASTS

class PodcastViewModel(mediaSessionConnection: MediaSessionConnection)
    : MediaItemViewModel(MediaID(TYPE_ALL_PODCASTS).asString(), mediaSessionConnection)
{
}