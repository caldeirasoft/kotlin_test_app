package com.caldeirasoft.basicapp.presentation.ui.episodeinfo

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository

class EpisodeInfoViewModel(val mediaItem: MediaBrowserCompat.MediaItem,
                           val episodeRepository: EpisodeRepository,
                           mediaSessionConnection: MediaSessionConnection)
    : ViewModel() {

    // episode
    var episodeData = MutableLiveData<Episode>()
    val episodeDb: LiveData<Episode>

    // section
    val _sectionData: MediatorLiveData<Int> = MediatorLiveData()
    val sectionData: LiveData<Int> = _sectionData

    // mediasession
    val mediaSessionConnection = mediaSessionConnection.also {
    }

    init {
        episodeDb = episodeRepository.get(mediaItem.mediaId.toString())

        _sectionData.addSource(episodeDb) { episode ->
            _sectionData.postValue(episode?.section ?: SectionState.ARCHIVE.value)
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun playEpisode() {
        /*
        val mediaDescription = MediaMetadataCompat.Builder()
                .from(episode)
                .build()
        val mediaItem = MediaBrowserCompat.MediaItem(mediaDescription.description, mediaDescription.flag)
        val nowPlaying = mediaSessionConnection.nowPlaying.value
        val transportControls = mediaSessionConnection.transportControls

        val isPrepared = mediaSessionConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared && (mediaItem.mediaId == nowPlaying?.id)) {

        }
        else {
            transportControls.playFromUri(mediaItem.description.mediaUri, null)
        }
        */
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