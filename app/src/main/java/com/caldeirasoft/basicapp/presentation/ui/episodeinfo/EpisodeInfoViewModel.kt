package com.caldeirasoft.basicapp.presentation.ui.episodeinfo

import android.support.v4.media.MediaBrowserCompat.MediaItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_EPISODE_ARCHIVE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_EPISODE_TOGGLE_FAVORITE

class EpisodeInfoViewModel(val episodeId: Long,
                           val episodeRepository: EpisodeRepository,
                           val mediaSessionConnection: MediaSessionConnection)
    : ViewModel() {

    // episode
    val episodeData: LiveData<Episode>

    // mediadata
    var mediaData = MutableLiveData<MediaItem>().apply {
        //this.value = mediaItem
    }

    // episode playing
    var isPlayingEpisode: MediatorLiveData<Boolean> = MediatorLiveData()

    // now playing
    var isNowPlaying: MediatorLiveData<Boolean> = MediatorLiveData()

    // episode section
    val sectionData: MediatorLiveData<Int> = MediatorLiveData()

    // init
    init {
        episodeData = episodeRepository.get(episodeId)

        sectionData.addSource(episodeData) { episode ->
            sectionData.postValue(episode?.section ?: SectionState.ARCHIVE.value)
        }

        /*
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
        */
    }

    fun playEpisode() {
        /*
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
         */
    }

    fun addToPlayNext() {
        // add to queue item
        //mediaSessionConnection.addQueueItem(mediaItem.description, 1)
    }

    fun addToQueueEnd() {
        //mediaSessionConnection.addQueueItem(mediaItem.description)
    }

    fun toggleFavorite() {
        sendCustomMediaItemCommand(COMMAND_CODE_EPISODE_TOGGLE_FAVORITE)
    }

    fun archiveEpisode() {
        sendCustomMediaItemCommand(COMMAND_CODE_EPISODE_ARCHIVE)
    }

    private fun sendCustomMediaItemCommand(customAction: String) {
        customAction
        /*
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

         */
    }
}