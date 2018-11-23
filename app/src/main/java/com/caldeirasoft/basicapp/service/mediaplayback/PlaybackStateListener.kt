package com.caldeirasoft.basicapp.service.mediaplayback

import android.support.v4.media.session.PlaybackStateCompat

interface PlaybackStateListener {
    fun updatePlaybackState(newState: PlaybackStateCompat)
    fun onPlaybackStart()
    fun onPlaybackStop()
    fun onNotificationRequired()
}