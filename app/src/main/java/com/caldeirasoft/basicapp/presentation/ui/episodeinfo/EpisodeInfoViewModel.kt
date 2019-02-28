package com.caldeirasoft.basicapp.presentation.ui.episodeinfo

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.session.MediaControllerCompat
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.service.playback.MediaService.Companion.EXTRA_PODCAST
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_QUEUE_ADD_ITEM
import com.caldeirasoft.castly.service.playback.extensions.id
import com.caldeirasoft.castly.service.playback.extensions.isPrepared
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EpisodeInfoViewModel(val mediaItem: MediaItem,
                           val episodeRepository: EpisodeRepository,
                           val mediaSessionConnection: MediaSessionConnection)
    : ViewModel() {

    // episode
    var episodeData = MutableLiveData<Episode>()
    val episodeDb: LiveData<Episode>

    // mediadata
    var mediaData = MutableLiveData<MediaItem>().apply {
        this.value = mediaItem
    }

    // episode section
    val _sectionData: MediatorLiveData<Int> = MediatorLiveData()
    val sectionData: LiveData<Int> = _sectionData

    // init
    init {
        episodeDb = episodeRepository.get(mediaItem.mediaId.orEmpty())

        _sectionData.addSource(episodeDb) { episode ->
            _sectionData.postValue(episode?.section ?: SectionState.ARCHIVE.value)
        }
    }

    fun playEpisode() {
        mediaSessionConnection.transportControls.playFromMediaId(mediaItem.mediaId, Bundle())
        val nowPlaying = mediaSessionConnection.nowPlaying.value
        val transportControls = mediaSessionConnection.transportControls

        val isPrepared = mediaSessionConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared && (mediaItem.mediaId == nowPlaying?.id)) {

        }
        else {
            val extra = Bundle().apply {
                putParcelable(EXTRA_PODCAST, mediaItem)
            }
            /*
            mediaSessionConnection.sendCustomAction(
                    COMMAND_CODE_QUEUE_ADD_ITEM,
                    extra,
                    object : MediaBrowserCompat.CustomActionCallback() {})
            */
            mediaSessionConnection.addQueueItem(mediaItem.description)
            //transportControls.playFromUri(mediaItem.description.mediaUri, null)
        }
    }

    fun pauseEpisode() {

    }

    fun addToPlayNext() {

    }

    fun addToQueueEnd() {

    }

    fun toggleFavorite() {

    }

    fun archiveEpisode() {

    }
}