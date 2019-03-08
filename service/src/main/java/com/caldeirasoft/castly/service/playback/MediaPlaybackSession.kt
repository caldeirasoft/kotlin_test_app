/*
 * Copyright 2017 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caldeirasoft.castly.service.playback

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.MediaSessionCompat.*
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.widget.MediaController
import androidx.core.app.NotificationManagerCompat
import androidx.media.session.MediaButtonReceiver
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.repository.FeedlyRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.service.BuildConfig
import com.caldeirasoft.castly.service.R
import com.caldeirasoft.castly.service.playback.NotificationBuilder.Companion.NOW_PLAYING_NOTIFICATION
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_PLAYBACK_UPDATE_INFO
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.CURRENT_PROGRESS
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.PROGRESS_UPDATE_EVENT
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.VOLUME_DUCK
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.VOLUME_NORMAL
import com.caldeirasoft.castly.service.playback.extensions.*
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player.DISCONTINUITY_REASON_PERIOD_TRANSITION
import com.google.android.exoplayer2.Player.STATE_ENDED
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.threeten.bp.ZoneOffset


/**
 * This class is the entry point for browsing and playback commands from the APP's UI
 * and other apps that wish to play music via UAMP (for example, Android Auto or
 * the Google Assistant).
 *
 * Browsing begins with the method [MusicService.onGetRoot], and continues in
 * the callback [MusicService.onLoadChildren].
 *
 * For more information on implementing a MediaBrowserService,
 * visit [https://developer.android.com/guide/topics/media-apps/audio-app/building-a-mediabrowserservice.html](https://developer.android.com/guide/topics/media-apps/audio-app/building-a-mediabrowserservice.html).
 */
class MediaPlaybackSession(context: Context,
                           val podcastRepository: PodcastRepository,
                           val episodeRepository: EpisodeRepository,
                           val feedlyRepository: FeedlyRepository
) : MediaSessionCompat.Callback(), AudioManager.OnAudioFocusChangeListener, Player.EventListener {

    private var mediaSession: MediaSessionCompat = MediaSessionCompat(context, BuildConfig.APPLICATION_ID)
    private val mediaButtonIntent =
            Intent(Intent.ACTION_MEDIA_BUTTON, null, context, MediaButtonReceiver::class.java)

    // Default parameters, except allowCrossProtocolRedirects is true
    val userAgent =  Util.getUserAgent(context, context.getString(R.string.app_name))
    private val httpDataSourceFactory = DefaultHttpDataSourceFactory(
            userAgent, null,
            DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
            true)
    private val dataSourceFactory =
            DefaultDataSourceFactory(context, DefaultBandwidthMeter(), httpDataSourceFactory)

    private val exoPlayer: SimpleExoPlayer =
            ExoPlayerFactory.newSimpleInstance(context)

    private val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val becomingNoisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)

    private val becomingNoisyReceiver: BecomingNoisyReceiver
    private var playOnFocusGain = false
    private lateinit var audioFocusRequest: AudioFocusRequest
    private var audioFocusRequested = false

    // Playlist items
    private var lastInitializedTrack: MediaDescriptionCompat? = null
    // exo-player media source : use as playlist for exoplayer
    private val mediaSource = ConcatenatingMediaSource()
    // queue list
    private val queueList = mutableListOf<MediaSessionCompat.QueueItem>()
    //private var queueIndex = -1 // queue index
    private val currentTrack: QueueItem?
        get() = queueList.firstOrNull()



    private val stateBuilder: PlaybackStateCompat.Builder = PlaybackStateCompat.Builder()
            .setActions(
                    PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_STOP
                            or PlaybackStateCompat.ACTION_PAUSE
                            or PlaybackStateCompat.ACTION_PLAY_PAUSE
                            or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                            or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)

    val session: MediaSessionCompat
        get() = mediaSession

    val token: MediaSessionCompat.Token
        get() = mediaSession.sessionToken

    val controller: MediaControllerCompat
        get() = mediaSession.controller

    init {
        // init ExoPlayer
        exoPlayer.addListener(this)
        exoPlayer.audioAttributes = AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build()

        // init AudioFocusRequest
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = android.media.AudioAttributes.Builder()
                    .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                    .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()

            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setOnAudioFocusChangeListener(this)
                    .setAcceptsDelayedFocusGain(false)
                    .setWillPauseWhenDucked(true)
                    .setAudioAttributes(audioAttributes)
                    .build()
        }

        // init MediaSession
        mediaSession.setCallback(this)
        mediaSession.setFlags(FLAG_HANDLES_MEDIA_BUTTONS or FLAG_HANDLES_TRANSPORT_CONTROLS or FLAG_HANDLES_QUEUE_COMMANDS)
        mediaSession.setMediaButtonReceiver(PendingIntent.getBroadcast(context, 0, mediaButtonIntent, 0))
        becomingNoisyReceiver = BecomingNoisyReceiver(context = context, sessionToken = mediaSession.sessionToken)
    }

    fun release() {
        stop()
        stateBuilder.build() // STATE_NONE
        mediaSession.release()
        exoPlayer.release()
    }

    private val updateIntervalMs = 1000L
    private val progressHandler = Handler()
    private var needUpdateProgress = false


    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var notificationBuilder: NotificationBuilder

    private val updateProgressTask = Runnable {
        if (needUpdateProgress) {
            val bundle = Bundle().apply {
                putLong(CURRENT_PROGRESS, exoPlayer.currentPosition)
            }
            mediaSession.sendSessionEvent(PROGRESS_UPDATE_EVENT, bundle)
            startUpdateProgress(true)
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        println("onAudioFocusChange(${focusChange._stringifyAudioFocusChange()})")
        if (focusChange < 0) { //focus lost
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS,
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    playOnFocusGain = false
                    if (exoPlayer.playWhenReady) {
                        onPause()
                        playOnFocusGain = focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                    }
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    exoPlayer.volume = VOLUME_DUCK
                    playOnFocusGain = true
                }
                AudioManager.AUDIOFOCUS_GAIN -> {

                }
            }
        }
        else {
            exoPlayer.volume = VOLUME_NORMAL
            exoPlayer.playWhenReady = playOnFocusGain
            playOnFocusGain = false
        }
    }

    private fun Int._stringifyAudioFocusChange(): String? {
        return AudioManager::class.java.declaredFields.filter {
            it.name.startsWith("AUDIOFOCUS_") && it.type == Int::class.java && it.get(null) == this
        }.firstOrNull()?.name
    }


    private fun Int._stringifyExoPlayerState() = when (this) {
        Player.STATE_BUFFERING -> "STATE_BUFFERING"
        Player.STATE_IDLE -> "STATE_IDLE"
        Player.STATE_ENDED -> "STATE_ENDED"
        Player.STATE_READY -> "STATE_READY"
        else -> "UNKNOWN"
    }

    private fun requestAudioFocus(): Boolean {
        if (!audioFocusRequested) {
            audioFocusRequested = true

            val audioFocusResult: Int =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) audioManager.requestAudioFocus(audioFocusRequest)
                    else audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)

            when (audioFocusResult) {
                AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                    becomingNoisyReceiver.register()
                    return true
                }
            }
        }
        return false
    }

    private fun abandonAudioFocus() {
        if (!audioFocusRequested) {
            return
        }
        audioFocusRequested = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest)
        } else {
            audioManager.abandonAudioFocus(this)
        }
    }


    private fun startUpdateProgress(fromRunnable: Boolean = false) {
        if (!fromRunnable && needUpdateProgress) {
            return
        }
        needUpdateProgress = true
        progressHandler.postDelayed(updateProgressTask, updateIntervalMs)
    }

    private fun stopUpdateProgress() {
        needUpdateProgress = false
        progressHandler.removeCallbacks(updateProgressTask)
    }

    private fun sendPlaylistInfoEvent() {
        val bundle = Bundle().apply {
            //putBoolean(HAS_NEXT, musicRepo?.hasNext == true)
            //putBoolean(HAS_PREV, musicRepo?.hasPrev == true)
        }
        //mediaSession?.sendSessionEvent(PLAYLIST_INFO_EVENT, bundle)
    }

    /**
     * region MediaSessionCallback events
     */

    private fun play() {
        println("play")

        if (currentTrack == null)
            return

        if (lastInitializedTrack?.mediaUri != currentTrack?.description?.mediaUri) {
            updateMetadata(currentTrack?.description)
        }

        onPlay()
    }

    override fun onPlay() {
        println("onPlay")

        if (!requestAudioFocus()) {
            exoPlayer.playWhenReady = false
            changeState(STATE_ERROR)
            return
        }

        exoPlayer.playWhenReady = true
        startUpdateProgress()
    }

    override fun onPause() {
        println("onPause")
        pause()
    }

    private fun pause() {
        exoPlayer.playWhenReady = false

        stopUpdateProgress()
        abandonAudioFocus()
    }

    override fun onStop() {
        println("onStop")
        stop()
    }

    private fun stop(){
        exoPlayer.stop()

        stopUpdateProgress()
        abandonAudioFocus()

        mediaSession.isActive = false
        //mediaSession.setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_STOPPED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1F).build())
    }

    fun onClear() {
        println("onClear")
        stop()

        queueList.clear()
        mediaSession.setQueue(queueList)
        mediaSource.clear()
        lastInitializedTrack = null

        mediaSession.apply {
            isActive = false
            setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_NONE, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1F).build())
            setMetadata(null)
        }
    }

    override fun onCommand(command: String?, extras: Bundle?, cb: ResultReceiver?) {
        println("onCommand")
        when (command) {
            COMMAND_PLAYBACK_UPDATE_INFO -> {
                //mediaSession.setMetadata(metadataBuilder.build())
                sendPlaylistInfoEvent()
            }
        }
    }

    override fun onCustomAction(action: String?, extras: Bundle?) {
        println("onCustomAction")
    }

    override fun onSkipToNext() {
        println("onSkipToNext")
        if (queueList.size == 1) {
            onClear()
        }
        else {
            queueList.removeAt(0)
            play()
        }
    }

    fun onPlayNext() {
        val removedItem = queueList.removeAt(0)
        upsertMediaItem(removedItem.description, SectionState.ARCHIVE.value, null)

        mediaSession.setQueue(queueList)
        updateMetadata(currentTrack?.description)
        saveCurrentPosition()
        //play()
    }

    fun onPlayEnd() {
        if (!queueList.isEmpty()) {
            val removedItem = queueList.removeAt(0)
            upsertMediaItem(removedItem.description, SectionState.ARCHIVE.value, null)
        }
    }

    override fun onSkipToPrevious() {
        println("onSkipToPrevious")
    }

    override fun onAddQueueItem(description: MediaDescriptionCompat) {
        println("onAddQueueItem")

        if (!queueList.any { item -> item.description.mediaId == description.mediaId })
        {
            queueList.add(QueueItem(description, description.mediaId.hashCode().toLong()))
            mediaSession.setQueue(queueList)
            mediaSource.addMediaSource(buildMediaSource(description, dataSourceFactory))
            upsertMediaItem(description, SectionState.QUEUE.value, queueList.size - 1)

            if (mediaSource.size == 1) {
                exoPlayer.prepare(mediaSource)
                play()
            }
        }
    }

    override fun onAddQueueItem(description: MediaDescriptionCompat, index: Int) {
        println("onAddQueueItem")

        if (!queueList.any { item -> item.description.mediaId == description.mediaId })
        {
            val offset = mediaSource.size - queueList.size
            queueList.add(index, QueueItem(description, description.mediaId.hashCode().toLong()))
            mediaSession.setQueue(queueList)
            mediaSource.addMediaSource(index + offset, buildMediaSource(description, dataSourceFactory))
            upsertMediaItem(description, SectionState.QUEUE.value, index)

            if (mediaSource.size == 1 || index == 0) {
                exoPlayer.prepare(mediaSource)
                play()
            }
        }
    }

    override fun onRemoveQueueItem(description: MediaDescriptionCompat) {
        println("onRemoveQueueItem")

        val index = queueList.indexOfFirst { it.description.mediaId == description.mediaId }
        if (index > -1) {
            if (index == 0) {
                //TODO: check if removed item is current item
                saveCurrentPosition()
            }

            val offset = mediaSource.size - queueList.size
            queueList.removeAt(index)
            mediaSource.removeMediaSource(index + offset)
            mediaSession.setQueue(queueList)
        }
    }

    fun saveCurrentPosition() {
        val duration = exoPlayer.duration
        val position = exoPlayer.currentPosition
        currentTrack?.description?.let {
            it.duration = duration


            GlobalScope.launch {
                episodeRepository.getSync(it.mediaId.orEmpty())?.let { episode ->
                    episode.duration = duration
                    episode.playbackPosition = position
                    episode.timePlayed?.let {
                        episode.timePlayed = org.threeten.bp.LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                    }
                    episodeRepository.update(episode)
                }
            }
        }
    }

    fun upsertMediaItem(description: MediaDescriptionCompat, section: Int, index: Int?) {
        GlobalScope.launch {
            description.toEpisode()?.let { episode ->
                episode.section = section
                episode.queuePosition = index
                episodeRepository.upsert(episode)
            }
        }
    }

    private fun buildMediaSource(mediaDescription: MediaDescriptionCompat, dataSourceFactory: DataSource.Factory): MediaSource {
        val uri = mediaDescription.mediaUri
        val type = Util.inferContentType(uri)
        return ExtractorMediaSource.Factory(dataSourceFactory)
                .setTag(mediaDescription)
                .createMediaSource(uri)
    }

    /**
     * ExoPlayer listener
     */
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        println("onExoPlayerStateChanged + ${playbackState._stringifyExoPlayerState()}")
        when (playbackState) {
            Player.STATE_IDLE -> {

            }
            Player.STATE_BUFFERING -> {
                changeState(STATE_BUFFERING)
            }

            Player.STATE_READY -> {
                if (playWhenReady) {
                    changeState(STATE_PLAYING)
                }
                else {
                    changeState(STATE_PAUSED)
                }
                // set duration on current
                val duration = exoPlayer.duration
                if (duration >= 0) {
                    //metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                    currentTrack?.description?.duration = duration
                    updateMetadata(currentTrack?.description)
                    saveCurrentPosition()
                }
            }
            Player.STATE_ENDED -> {
                // update pos on last played
                saveCurrentPosition()
                onPlayEnd()
                // stop playback
                changeState(STATE_ENDED)
                updateMetadata(null)
                onClear()

            }
        }
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
        println("onPlaybackParametersChanged")
    }

    override fun onSeekProcessed() {
        println("onSeekProcessed")
    }

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
        println("onTracksChanged")
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        println("onPlayerError")
    }

    override fun onLoadingChanged(isLoading: Boolean) {
        println("onLoadingChanged")
    }

    override fun onPositionDiscontinuity(reason: Int) {
        println("onPositionDiscontinuity + $reason")
        saveCurrentPosition()
        when (reason) {
            DISCONTINUITY_REASON_PERIOD_TRANSITION -> onPlayNext()
            else -> {}
        }
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        println("onRepeatModeChanged")
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        println("onShuffleModeEnabledChanged")
    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
        println("onTimelineChanged")
    }

    /**
     * Custom methods
     */
    private fun updateMetadata(desc: MediaDescriptionCompat?) {
        MediaMetadataCompat.Builder().apply {
            desc?.let {
                id = it.mediaId.orEmpty()
                title = it.title.toString()
                artist = it.artist
                album = it.album
                displayDescription = it.displayDescription
                albumArtUri = it.albumArtUri.toString()
                mediaUri = it.mediaUri.toString()
                date = it.date
                duration = it.duration
                currentPosition = it.currentPosition
                trackNumber = it.trackNumber

            }
            mediaSession.setMetadata(this.build())
        }
        lastInitializedTrack = desc
    }

    private fun changeState(state: Int, opts: (PlaybackStateCompat.Builder) -> Unit = {}) {
        val builder = stateBuilder
        builder.setActions(generateActions(state))
        applyCommonState(state, builder)
        opts(builder)
        val playbackState = builder.build()
        mediaSession.setPlaybackState(playbackState)
    }

    private fun updateState(current: PlaybackStateCompat) {
        val builder = stateBuilder
        builder.setActions(generateActions(current.state))
        applyCommonState(current.state, builder)
        val playbackState = builder.build()
        mediaSession.setPlaybackState(playbackState)
    }

    private fun applyCommonState(state: Int, builder: PlaybackStateCompat.Builder) {
        builder.setState(state, exoPlayer.currentPosition, exoPlayer.playbackParameters.speed)
        builder.setBufferedPosition(exoPlayer.bufferedPosition)
    }

    private fun generateActions(state: Int): Long {
        return when (state) {
            STATE_PLAYING,
            STATE_BUFFERING -> ACTION_PAUSE or getSeekableAction() or getQueueActions()

            STATE_PAUSED -> ACTION_PLAY or getSeekableAction() or getQueueActions()

            STATE_SKIPPING_TO_NEXT,
            STATE_SKIPPING_TO_PREVIOUS,
            STATE_SKIPPING_TO_QUEUE_ITEM -> ACTION_PAUSE

            STATE_FAST_FORWARDING,
            STATE_REWINDING -> ACTION_PLAY or ACTION_PAUSE

            STATE_ERROR,
            STATE_STOPPED,
            STATE_NONE -> 0

            else -> 0
        } or ACTION_PLAY_FROM_MEDIA_ID
    }

    private fun getSeekableAction(): Long {
        return if (exoPlayer.isCurrentWindowSeekable) {
            ACTION_SEEK_TO
        } else 0L
    }

    private fun getQueueActions(): Long {
        return ACTION_SKIP_TO_NEXT or
                ACTION_SKIP_TO_PREVIOUS or
                ACTION_SKIP_TO_QUEUE_ITEM
    }

    /**
     * Helper class for listening for when headphones are unplugged (or the audio
     * will otherwise cause playback to become "noisy").
     */
    private inner class BecomingNoisyReceiver(private val context: Context,
                                        sessionToken: MediaSessionCompat.Token)
        : BroadcastReceiver() {

        private val noisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        private val controller = MediaControllerCompat(context, sessionToken)

        private var registered = false

        fun register() {
            if (!registered) {
                context.registerReceiver(this, noisyIntentFilter)
                registered = true
            }
        }

        fun unregister() {
            if (registered) {
                context.unregisterReceiver(this)
                registered = false
            }
        }

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                controller.transportControls.pause()
            }
        }
    }

    /**
     * Inner class to receive callbacks about session changes to the [MediaSessionCompat]. In response
     * to those callbacks, this class:
     */
    private inner class MediaSessionCallback(val dataSourceFactory: DataSource.Factory) : MediaSessionCompat.Callback() {

    }
}