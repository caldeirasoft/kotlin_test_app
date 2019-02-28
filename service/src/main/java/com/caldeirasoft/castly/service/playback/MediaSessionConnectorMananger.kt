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

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.service.R
import com.caldeirasoft.castly.service.playback.extensions.*
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.DefaultPlaybackController
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueEditor
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * Class to bridge UAMP to the ExoPlayer MediaSession extension.
 */
class MediaSessionConnectorMananger(val context: Context, val repository: EpisodeRepository, val mediaSession: MediaSessionCompat)
{
    companion object {
        const val DEFAULT_REWIND_MS = 10000L
        const val DEFAULT_FAST_FORWARD_MS = 30000L
    }

    private var backendItems: HashMap<String, MediaBrowserCompat.MediaItem> = hashMapOf()

    private val playList = ArrayList<MediaDescriptionCompat>()
    private val eventListener = EventListener()
    private val mediaConnector: MediaSessionConnector
    private val playbackPreparer: PodcastPlaybackPreparer
    private val dataSourceFactory: DefaultDataSourceFactory
    private val mediaSource = ConcatenatingMediaSource()

    private val uAmpAudioAttributes = AudioAttributes.Builder()
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

    // Wrap a SimpleExoPlayer with a decorator to handle audio focus for us.
    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayerFactory.newSimpleInstance(context).apply {
            setAudioAttributes(uAmpAudioAttributes, false)
        }
    }


    init {
        exoPlayer.addListener(eventListener)

        val userAgent =  Util.getUserAgent(context, context.getString(R.string.app_name))

        // Default parameters, except allowCrossProtocolRedirects is true
        val httpDataSourceFactory = DefaultHttpDataSourceFactory(
                userAgent, null,
                DEFAULT_CONNECT_TIMEOUT_MILLIS, DEFAULT_READ_TIMEOUT_MILLIS,
                true)

        // Produces DataSource instances through which media data is loaded.
        dataSourceFactory = DefaultDataSourceFactory(context, DefaultBandwidthMeter(), httpDataSourceFactory)

        // Produces Extractor instances for parsing the media data.
        val extractorsFactory = DefaultExtractorsFactory()
        // The MediaSource represents the media to be played.
        val extractorMediaFactory = ExtractorMediaSource.Factory(dataSourceFactory)
        extractorMediaFactory.setExtractorsFactory(extractorsFactory)

        val queueDataAdapter = CustomQueueAdapter()
        val mediaSourceFactory = object : TimelineQueueEditor.MediaSourceFactory {
            override fun createMediaSource(description: MediaDescriptionCompat?): MediaSource? {
                return description?.mediaUri?.let {
                    extractorMediaFactory
                            .setTag(description)
                            .createMediaSource(it)
                }
            }
        }

        playbackPreparer = PodcastPlaybackPreparer(repository, exoPlayer, dataSourceFactory)
        mediaConnector = MediaSessionConnector(mediaSession,
                DefaultPlaybackController(
                        DEFAULT_REWIND_MS,
                        DEFAULT_FAST_FORWARD_MS,
                        MediaSessionConnector.DEFAULT_REPEAT_TOGGLE_MODES)).also {
            it.setPlayer(exoPlayer, null)
            it.setQueueNavigator(QueueNavigator(mediaSession))
            it.setQueueEditor(TimelineQueueEditor(mediaSession.controller, mediaSource, queueDataAdapter, mediaSourceFactory))
        }
    }

    fun addToBackstore(mediaItem: MediaBrowserCompat.MediaItem) {
        backendItems.put(mediaItem.mediaId.orEmpty(), mediaItem)
    }

    /**
     * Helper class to retrieve the the Metadata necessary for the ExoPlayer MediaSession connection
     * extension to call [MediaSessionCompat.setMetadata].
     */
    private inner class QueueNavigator(mediaSession: MediaSessionCompat) : TimelineQueueNavigator(mediaSession) {

        private val window = Timeline.Window()
        private var isLoading = false

        override fun onSkipToNext(player: Player?) {
            super.onSkipToNext(player)
        }

        override fun onSkipToPrevious(player: Player?) {
            super.onSkipToPrevious(player)
        }

        override fun onSkipToQueueItem(player: Player?, id: Long) {
            super.onSkipToQueueItem(player, id)
        }

        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return player.currentTimeline.getWindow(windowIndex, window, true).tag as MediaDescriptionCompat
        }
    }

    private inner class CustomQueueAdapter : TimelineQueueEditor.QueueDataAdapter {
        override fun getMediaDescription(position: Int): MediaDescriptionCompat = playList[position]

        override fun add(position: Int, description: MediaDescriptionCompat) {
            if (mediaSource.size == 1)
                playList.clear()

            playList.add(position, description)
            if (playList.size == 1) {
                exoPlayer.prepare(mediaSource)
                exoPlayer.seekTo(0)
                exoPlayer.playWhenReady = true
            }
        }

        override fun remove(position: Int) {
            playList.removeAt(position)
        }

        override fun move(from: Int, to: Int) {
            val mediaItem = playList.removeAt(from)
            playList.add(to, mediaItem)
        }
    }

    private inner class EventListener : Player.EventListener {
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
            println("onPositionDiscontinuity")
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

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            println("onPlayerStateChanged")
            /*
            when (playbackState) {
                STATE_BUFFERING,
                STATE_READY -> {
                    mediaSession.isActive = true
                }
                else -> mediaSession.isActive = false
            }
            */
        }
    }
}