package com.caldeirasoft.basicapp.presentation.ui.episodeinfo

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media2.MediaItem
import androidx.versionedparcelable.ParcelUtils
import com.caldeirasoft.basicapp.media.MediaBrowserConnectionCallback
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.service.playback.PodcastLibraryService.Companion.EXTRA_PODCAST
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EpisodeInfoViewModel(val mediaItem: MediaItem,
                           val episodeRepository: EpisodeRepository,
                           mediaSessionConnection: MediaSessionConnection)
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

    // media browser
    val browser = mediaSessionConnection.getMediaBrowser(object : MediaBrowserConnectionCallback(){})

    // init
    init {
        episodeDb = episodeRepository.get(mediaItem.metadata?.mediaId.orEmpty())

        _sectionData.addSource(episodeDb) { episode ->
            _sectionData.postValue(episode?.section ?: SectionState.ARCHIVE.value)
        }
    }

    fun playEpisode() {
        GlobalScope.launch {
            if (browser.isConnected) {
                browser.setPlaylist(arrayListOf(mediaItem.metadata?.mediaId.orEmpty()), null)
                browser.skipToPlaylistItem(0)
            }
        }
        /*
        val mediaDescription = MediaMetadataCompat.Builder()
                .from(episode)
                .build()
        val mediaItem = MediaBrowserCompat.MediaItem(mediaDescription.description, mediaDescription.flag)
        val nowPlaying = mediaSessionConnection.nowPlaying.value
        val transportControls = mediaSessionConnection.transportControls

        val isPrepared = mediaSessionConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared && (mediaItem.id == nowPlaying?.id)) {

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