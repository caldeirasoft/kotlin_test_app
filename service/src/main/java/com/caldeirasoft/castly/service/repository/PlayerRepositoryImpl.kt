package com.caldeirasoft.castly.service.repository;

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.session.MediaBrowser
import androidx.media2.session.MediaController
import androidx.media2.session.SessionCommandGroup
import androidx.media2.session.SessionToken
import com.caldeirasoft.castly.data.datasources.local.dao.EpisodeDao
import com.caldeirasoft.castly.domain.model.entities.Episode
import com.caldeirasoft.castly.domain.repository.PlayerRepository

/**
 * Created by Edmond on 15/02/2018.
 */

class PlayerRepositoryImpl(val episodeDao: EpisodeDao, val context: Context, serviceComponent: ComponentName) : PlayerRepository
{
    private var sessionToken: SessionToken
    private var mediaBrowser: MediaBrowser

    init {
        sessionToken = SessionToken(context, serviceComponent)
        mediaBrowser = androidx.media2.session.MediaBrowser.Builder(context)
                .setControllerCallback(ContextCompat.getMainExecutor(context), MediaBrowserConnectionCallback())
                .setSessionToken(sessionToken)
                .build()
    }

    /**
     * Get currently playing episode id
     */
    var currentMediaIdLiveData = MutableLiveData<String>().apply { value = null }
    override val currentMediaId: LiveData<String?>
        get() = currentMediaIdLiveData

    /**
     * Get currently playing episode
     */
    var currentMediaEpisodeLiveData = MutableLiveData<Episode>()
    override val currentMediaEpisode: LiveData<Episode?>
        get() = currentMediaEpisodeLiveData

    /**
     * Get current playlist item Ids
     */
    var playlistItemIdsLiveData = MutableLiveData<List<String>>().apply { value = null }
    override val playlistItemIds: LiveData<List<String>>
        get() = playlistItemIdsLiveData

    /**
     * Currrent position
     */
    override val currentPosition: Long
        get() = mediaBrowser.currentPosition

    /**
     * Currrent duration
     */
    override val currentDuration: Long
        get() = mediaBrowser.duration

    /**
     * Get current player state
     */
    var playerStateLiveData = MutableLiveData<Int>()
    override val playerState: LiveData<Int>
        get() = playerStateLiveData

    /**
     * Get current player state
     */
    var bufferingStateLiveData = MutableLiveData<Int>()
    override val bufferingState: LiveData<Int>
        get() = bufferingStateLiveData

    /**
     * Play an episode
     */
    override fun playEpisode(episodeId: String) {
        if (mediaBrowser.playlist.isNullOrEmpty()) {
            playSingleEpisodePlaylist(episodeId)
        }
    }

    /**
     * Add episode to queue
     */
    override fun queueNextEpisode(episodeId: String) {
        mediaBrowser.playlist?.let {
            // list not empty
            mediaBrowser.addPlaylistItem(1, episodeId)
        }
                // empty list
                ?: apply {
                    playSingleEpisodePlaylist(episodeId)
                }
    }

    /**
     * Add episode to end of queue
     */
    override fun queueLastEpisode(episodeId: String) {
        mediaBrowser.playlist?.let {
            // list not empty
            mediaBrowser.addPlaylistItem(it.size - 1, episodeId)
        }
                // empty list
                ?: apply {
                    playSingleEpisodePlaylist(episodeId)
                }

    }

    private fun playSingleEpisodePlaylist(episodeId: String) {
        mediaBrowser.setPlaylist(arrayListOf(episodeId),
                MediaMetadata.Builder()
                        .putText(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, "playlist")
                        .build())
        mediaBrowser.play()
    }

    /**
     * Move episode into queue
     */
    override fun moveEpisode(episodeId: String, targetIndex: Int) {

    }

    /**
     * Remove episode from queue
     */
    override fun removeFromQueue(episodeId: String) {

    }

    /**
     * Play
     */
    override fun play() {
        mediaBrowser.play()
    }

    /**
     * Pause
     */
    override fun pause() {
        mediaBrowser.pause()
    }

    /**
     * Fast forward
     */
    override fun fastForward() {}

    /**
     * Rewind
     */
    override fun rewind() {}

    /**
     * Media browser callback class
     */
    inner class MediaBrowserConnectionCallback : MediaBrowser.BrowserCallback() {
        val isConnected = MutableLiveData<Boolean>()
                .apply { postValue(false) }
        var onConnectedAction: (() -> Unit)? = null

        fun setConnectionAction(connectedAction: (() -> Unit)?) {
            this.onConnectedAction = connectedAction
        }

        override fun onConnected(controller: MediaController, allowedCommands: SessionCommandGroup) {
            super.onConnected(controller, allowedCommands)
            Log.d("PlayRepository", "onConnected")
        }

        override fun onCurrentMediaItemChanged(controller: MediaController, item: MediaItem?) {
            Log.d("PlayerRepository", "onCurrentMediaItemChanged")
            super.onCurrentMediaItemChanged(controller, item)
            currentMediaIdLiveData.postValue(item?.metadata?.mediaId)
        }

        override fun onPlayerStateChanged(controller: MediaController, state: Int) {
            Log.d("PlayerRepository", "onPlayerStateChanged")
            super.onPlayerStateChanged(controller, state)
            playerStateLiveData.postValue(state)
        }

        override fun onBufferingStateChanged(controller: MediaController, item: MediaItem, state: Int) {
            Log.d("PlayerRepository", "onBufferingStateChanged")
            super.onBufferingStateChanged(controller, item, state)
            if (item == controller.currentMediaItem)
                bufferingStateLiveData.postValue(state)
        }

        override fun onPlaylistChanged(controller: MediaController, list: MutableList<MediaItem>?, metadata: MediaMetadata?) {
            Log.d("PlayerRepository", "onPlaylistChanged")
            super.onPlaylistChanged(controller, list, metadata)
            list?.map { item -> item.metadata?.mediaId.orEmpty() }.let {
                playlistItemIdsLiveData.postValue(it)
            }
        }
    }
}