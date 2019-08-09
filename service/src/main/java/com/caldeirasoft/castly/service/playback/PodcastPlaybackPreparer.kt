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

import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.service.playback.extensions.mediaMetaData
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * Class to bridge UAMP to the ExoPlayer MediaSession extension.
 */
class PodcastPlaybackPreparer(val repository: EpisodeRepository,
                              val exoPlayer: ExoPlayer,
                              val dataSourceFactory: DataSource.Factory
) : MediaSessionConnector.PlaybackPreparer
{
    var backend: HashMap<String, MediaBrowserCompat.MediaItem> = hashMapOf()

    override fun getSupportedPrepareActions(): Long =
        PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID

    override fun onPrepare() {
        println("PodcastPlaybackPreparer - onPrepare")
    }

    override fun onPrepareFromMediaId(mediaId: String, extras: Bundle?) {

        val mediaSource = ConcatenatingMediaSource()

        val itemBackend = backend[mediaId]
        if (itemBackend != null) {
            mediaSource.addMediaSource(buildMediaSource(itemBackend.description, dataSourceFactory))
            exoPlayer.prepare(mediaSource)
            exoPlayer.seekTo(0)
        }
        else {
            GlobalScope.launch {
                val itemToPlay = repository.getSync(mediaId.toLong())
                if (itemToPlay != null) {
                    val metadata = itemToPlay.mediaMetaData
                    mediaSource.addMediaSource(buildMediaSource(metadata, dataSourceFactory))
                }
                exoPlayer.prepare(mediaSource)
                exoPlayer.seekTo(0)
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

    private fun buildMediaSource(metadata: MediaMetadataCompat, dataSourceFactory: DataSource.Factory): MediaSource {
        val uri = metadata.description.mediaUri
        val type = Util.inferContentType(uri)
        val tag = metadata.description.also {
            it.extras?.putAll(metadata.bundle)
        }
        return ExtractorMediaSource.Factory(dataSourceFactory)
                .setTag(tag)
                .createMediaSource(uri)
    }

    override fun onPrepareFromSearch(query: String?, extras: Bundle?) = Unit

    override fun onPrepareFromUri(uri: Uri?, extras: Bundle?) = Unit

    override fun getCommands(): Array<String>? = null

    override fun onCommand(player: Player?, command: String?, extras: Bundle?, cb: ResultReceiver?) = Unit
}