package com.caldeirasoft.basicapp.service_old.mediaplayback

interface PlayerListener {
    /**
     * On current music completed.
     */
    fun onCompletion()

    /**
     * on Playback status changed
     * Implementations can use this callback to update
     * playback state on the media sessions.
     */
    fun onPlaybackStatusChanged(state: Int)
}