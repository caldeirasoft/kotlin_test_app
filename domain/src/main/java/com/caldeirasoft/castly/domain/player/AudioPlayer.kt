package com.caldeirasoft.castly.domain.player

import androidx.lifecycle.LiveData
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.player.MediaInfo
import com.caldeirasoft.castly.domain.model.player.PlayerInfo
import kotlinx.coroutines.Deferred

interface AudioPlayer {
    val playerInfo: LiveData<PlayerInfo>
    val mediaInfo: LiveData<MediaInfo>

    fun play(): Deferred<Unit>
    fun pause(): Deferred<Unit>
    fun skipForward(): Deferred<Unit>
    fun skipBackwards(): Deferred<Unit>
    fun seekTo(pos: Long): Deferred<Unit>
    fun setPlaylistAndPlay(newPlaylist: List<Episode>, startPlayingId: Int): Deferred<Unit>
    fun tearDown()
}