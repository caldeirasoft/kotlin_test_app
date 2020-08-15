package com.caldeirasoft.castly.domain.repository;

import androidx.lifecycle.LiveData
import com.caldeirasoft.castly.domain.model.entities.Episode

/**
 * Created by Edmond on 15/02/2018.
 */

interface PlayerRepository {

    /**
     * Get currently playing episode id
     */
    val currentMediaId: LiveData<String?>

    /**
     * Get currently playing episode id
     */
    val currentMediaEpisode: LiveData<Episode?>

    /**
     * Get current playlist item Ids
     */
    val playlistItemIds: LiveData<List<String>>

    /**
     * Get current player position
     */
    val currentPosition: Long

    /**
     * Get current player duration
     */
    val currentDuration: Long

    /**
     * Get current player state
     */
    val playerState: LiveData<Int>

    /**
     * Get current player buffering state
     */
    val bufferingState: LiveData<Int>

    /**
     * Play an episode
     */
    fun playEpisode(episodeId: String)

    /**
     * Add episode to queue
     */
    fun queueNextEpisode(episodeId: String)

    /**
     * Add episode to end of queue
     */
    fun queueLastEpisode(episodeId: String)

    /**
     * Move episode into queue
     */
    fun moveEpisode(episodeId: String, targetIndex: Int)

    /**
     * Remove episode from queue
     */
    fun removeFromQueue(episodeId: String)

    /**
     * Play
     */
    fun play()

    /**
     * Pause
     */
    fun pause()

    /**
     * Fast forward
     */
    fun fastForward()

    /**
     * Rewind
     */
    fun rewind()
}