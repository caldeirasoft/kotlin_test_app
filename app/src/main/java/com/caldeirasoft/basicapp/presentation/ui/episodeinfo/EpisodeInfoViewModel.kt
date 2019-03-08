package com.caldeirasoft.basicapp.presentation.ui.episodeinfo

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.EXTRA_MEDIA_ID
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.service.playback.const.Constants
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_QUEUE_ADD_ITEM
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.EXTRA_EPISODE
import com.caldeirasoft.castly.service.playback.extensions.id
import com.caldeirasoft.castly.service.playback.extensions.isPlaying
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

    // episode playing
    var isPlayingEpisode: MediatorLiveData<Boolean> = MediatorLiveData()

    // now playing
    var isNowPlaying: MediatorLiveData<Boolean> = MediatorLiveData()

    // episode section
    val sectionData: MediatorLiveData<Int> = MediatorLiveData()

    // init
    init {
        episodeDb = episodeRepository.get(mediaItem.mediaId.orEmpty())

        sectionData.addSource(episodeDb) { episode ->
            sectionData.postValue(episode?.section ?: SectionState.ARCHIVE.value)
        }

        isNowPlaying.apply {
                addSource(mediaSessionConnection.nowPlaying) { nowPlaying ->
                    postValue(getIsNowPlaying(nowPlaying, mediaSessionConnection.playbackState.value))
                }
                addSource(mediaSessionConnection.playbackState) { playbackState ->
                    postValue(getIsNowPlaying(mediaSessionConnection.nowPlaying.value, playbackState))
                }
        }

        isPlayingEpisode.addSource(mediaSessionConnection.nowPlaying) { nowPlaying ->
            isPlayingEpisode.postValue(mediaItem.mediaId == nowPlaying?.id)
        }
    }

    fun playEpisode() {
        val nowPlaying = mediaSessionConnection.nowPlaying.value
        val transportControls = mediaSessionConnection.transportControls

        val isPrepared = mediaSessionConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared && isPlayingEpisode.value == true) {
            if (isNowPlaying.value == true)
                transportControls.pause()
            else
                transportControls.play()
        }
        else {
            // add to queue item
            mediaSessionConnection.addQueueItem(mediaItem.description, 0)
        }
    }

    fun addToPlayNext() {
        // add to queue item
        mediaSessionConnection.addQueueItem(mediaItem.description, 1)
    }

    fun addToQueueEnd() {
        mediaSessionConnection.addQueueItem(mediaItem.description)
    }

    fun toggleFavorite() {
        sendCustomMediaItemCommand(Constants.COMMAND_CODE_EPISODE_TOGGLE_FAVORITE)
    }

    fun archiveEpisode() {
        sendCustomMediaItemCommand(Constants.COMMAND_CODE_EPISODE_ARCHIVE)
    }

    private fun sendCustomMediaItemCommand(customAction: String) {
        val extra = Bundle().apply {
            // add media ID
            this.putString(EXTRA_MEDIA_ID, mediaItem.mediaId)
        }
        mediaSessionConnection.sendCustomAction(customAction, extra,
                object : MediaBrowserCompat.CustomActionCallback() {
                    override fun onResult(action: String?, extras: Bundle?, resultData: Bundle?) {
                        val mediaItem = resultData?.getParcelable<MediaItem>(EXTRA_EPISODE)
                        mediaData.postValue(mediaItem)
                    }
                })
    }

    private fun getIsNowPlaying(nowPlaying: MediaMetadataCompat?, playbackState: PlaybackStateCompat?): Boolean =
            nowPlaying?.id == mediaItem.mediaId && playbackState?.isPlaying == true
}