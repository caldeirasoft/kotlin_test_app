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

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.MediaDescriptionCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.media2.*
import androidx.media2.MediaController.ControllerResult.RESULT_CODE_BAD_VALUE
import androidx.media2.MediaController.ControllerResult.RESULT_CODE_SUCCESS
import androidx.media2.MediaMetadata.*
import androidx.media2.exoplayer.external.C
import androidx.media2.exoplayer.external.ExoPlayer
import androidx.media2.exoplayer.external.ExoPlayerFactory
import androidx.media2.exoplayer.external.Player
import androidx.media2.exoplayer.external.audio.AudioAttributes
import androidx.media2.exoplayer.external.source.ConcatenatingMediaSource
import androidx.versionedparcelable.ParcelUtils
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.MediaID
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.GetEpisodesFromFeedlyUseCase
import com.caldeirasoft.castly.service.R
import com.caldeirasoft.castly.service.playback.extensions.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.concurrent.Executors


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
@SuppressLint("RestrictedApi")
class PodcastLibraryService : MediaLibraryService() {
    private lateinit var mediaSession: MediaLibrarySession
    private lateinit var mediaController: MediaController

    private val podcastRepository: PodcastRepository by inject()
    private val episodeRepository: EpisodeRepository by inject()
    private val getEpisodesFromFeedlyUseCase: GetEpisodesFromFeedlyUseCase by inject()

    companion object {
        const val ID = "TEST"

        const val MEDIA_ID_ARG = "MEDIA_ID"
        const val MEDIA_TYPE_ARG = "MEDIA_TYPE"
        const val MEDIA_CALLER = "MEDIA_CALLER"
        const val MEDIA_ID_ROOT = -1
        const val TYPE_ALL_PODCASTS = 0
        const val TYPE_QUEUE = 1
        const val TYPE_INBOX = 2
        const val TYPE_FAVORITE = 3
        const val TYPE_HISTORY = 4
        const val TYPE_PODCAST = 9
        const val TYPE_ALL_FOLDERS = 13
        const val TYPE_ALL_GENRES = 14
        const val TYPE_GENRE = 15

        const val EXTRA_PODCAST = "android.media.browse.extra.podcast"
        const val EXTRA_CONTINUATION = "android.media.browse.extra.continuation"
        const val EXTRA_SECTION = "android.media.browse.extra.section"

        const val NOTIFICATION_ID = 888

        const val MEDIA_ID_GET_ITEM = "media_id_get_item"

        val ROOT_ITEM: MediaItem
        val ROOT_PARAMS: LibraryParams

        init {
            ROOT_ITEM = MediaItem.Builder().setMetadata(
                    MediaMetadata.Builder().apply {
                        id = MediaID(MEDIA_ID_ROOT).asString()
                        browsable = BROWSABLE_TYPE_MIXED
                        playable = 0
                    }.build()).build()
            ROOT_PARAMS = LibraryParams.Builder()
                    .setExtras(Bundle().apply {
                        putString(ID, ID)
                    }).build()
        }
    }


    private var isForegroundService = false

    private val uAmpAudioAttributes = AudioAttributes.Builder()
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

    // Wrap a SimpleExoPlayer with a decorator to handle audio focus for us.
    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayerFactory.newSimpleInstance(this).apply {
            setAudioAttributes(uAmpAudioAttributes, false)
        }
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onGetSession(): MediaLibrarySession {
        // Build a PendingIntent that can be used to launch the UI.
        val sessionIntent = packageManager?.getLaunchIntentForPackage(packageName)
        val sessionActivityPendingIntent = PendingIntent.getActivity(this, 0, sessionIntent, 0)

        // create a new media player
        val mediaPlayer = MediaPlayer(this)

        val ioExecutor = Executors.newFixedThreadPool(5)

        val sessionCallback = SessionCallback()
        // Create a new MediaSession.
        mediaSession = MediaLibrarySession.Builder(this,
                mediaPlayer,
                ioExecutor,
                sessionCallback)
                .setId(ID)
                .build()

        val controllerCallback = MediaControllerCallBack()
        // Because ExoPlayer will manage the MediaSession, add the service as a callback for
        // state changes.
        mediaController = MediaController(this, mediaSession.token, ioExecutor, controllerCallback)

        return mediaSession
    }

    private inner class SessionCallback : MediaLibrarySession.MediaLibrarySessionCallback() {
        override fun onGetLibraryRoot(session: MediaLibrarySession, controller: MediaSession.ControllerInfo, params: LibraryParams?): LibraryResult {
            return LibraryResult(RESULT_CODE_SUCCESS, ROOT_ITEM, ROOT_PARAMS)
        }

        override fun onGetItem(session: MediaLibrarySession, controller: MediaSession.ControllerInfo, mediaId: String): LibraryResult {
            return if (mediaId == MEDIA_ID_GET_ITEM) {
                LibraryResult(RESULT_CODE_SUCCESS, createMediaItem(mediaId), null)
            }
            else {
                LibraryResult(RESULT_CODE_BAD_VALUE)
            }
        }

        override fun onGetChildren(session: MediaLibrarySession, controller: MediaSession.ControllerInfo, parentId: String, page: Int, pageSize: Int, params: LibraryParams?): LibraryResult {
            val mediaItems = ArrayList<MediaItem>()
            val mediaIdParent = MediaID().fromString(parentId)
            val mediaType = mediaIdParent.type
            val mediaId = mediaIdParent.mediaId
            val caller = mediaIdParent.caller

            when(mediaType) {
                MEDIA_ID_ROOT -> {
                    addMediaRoots(mediaItems, caller!!)
                    return LibraryResult(RESULT_CODE_SUCCESS, mediaItems, null)
                }
                TYPE_ALL_PODCASTS -> {
                    return onGetPodcasts(podcastRepository.fetchSync())
                }
                TYPE_INBOX -> {
                    return onLoadEpisodes(episodeRepository.fetchSync(SectionState.INBOX.value))
                }
                TYPE_FAVORITE -> {
                    return onLoadEpisodes(episodeRepository.fetchSync(SectionState.FAVORITE.value))
                }
                TYPE_HISTORY -> {
                    return onLoadEpisodes(episodeRepository.fetchSync(SectionState.HISTORY.value))
                }
                TYPE_PODCAST -> {
                    val section = params?.extras?.getInt(EXTRA_SECTION)
                    when (section) {
                        SectionState.QUEUE.value,
                        SectionState.INBOX.value,
                        SectionState.FAVORITE.value,
                        SectionState.HISTORY.value -> {
                            return onLoadEpisodes(episodeRepository.fetchSync(section, mediaId.toString()))
                        }
                        else -> {
                            val continuation = params?.extras?.getString(EXTRA_CONTINUATION) ?: ""
                            val episodes : MutableList<Episode> = arrayListOf()
                            onLoadEpisodesFromFeedly(params, pageSize, continuation, episodes)
                            return onLoadEpisodes(episodes)
                        }
                    }
                }
                else -> {
                    return LibraryResult(RESULT_CODE_BAD_VALUE)
                }
            }
        }

        fun onGetPodcasts(podcasts: List<Podcast>) : LibraryResult {
            val mediaItems = podcasts.map { MediaItem.Builder().setMetadata(it.mediaMetadata).build() }
            return LibraryResult(RESULT_CODE_SUCCESS, mediaItems, null)
        }

        fun onLoadEpisodes(episodes: List<Episode>) : LibraryResult {
            val mediaItems = episodes.map { MediaItem.Builder().setMetadata(it.mediaMetadata).build() }
            return LibraryResult(RESULT_CODE_SUCCESS, mediaItems, null)
        }

        fun onLoadEpisodesFromFeedly(
                params: LibraryParams?,
                pageSize: Int,
                continuation: String?,
                alreadyRetrievedEpisodes: MutableList<Episode> = java.util.ArrayList()) {

            GlobalScope.launch{
                val mediaMetadata = ParcelUtils.getVersionedParcelable<MediaMetadata>(params?.extras, EXTRA_PODCAST)
                mediaMetadata?.let {
                    val podcast: Podcast = mediaMetadata.toPodcast()

                    getEpisodesFromFeedlyUseCase
                            .execute(GetEpisodesFromFeedlyUseCase.Params(podcast, pageSize, continuation ?: ""))
                            .await()
                            .data
                            ?.let {
                                alreadyRetrievedEpisodes.addAll(it.data)
                                if (!it.continuation.isEmpty() &&
                                        (it.data.size < pageSize - alreadyRetrievedEpisodes.size)) {
                                    onLoadEpisodesFromFeedly(params, pageSize, it.continuation, alreadyRetrievedEpisodes)
                                } else {
                                    params?.extras?.putString(EXTRA_CONTINUATION, continuation)
                                }
                            }
                }
                Unit
            }
        }

        fun addMediaRoots(mediaRoot: MutableList<MediaItem>, caller:String) {
            mediaRoot.apply {
                // podcasts
                add(MediaItem.Builder()
                        .setMetadata(MediaMetadata.Builder().apply {
                            id = MediaID(TYPE_ALL_PODCASTS, null, caller).asString()
                            title = getString(R.string.podcasts)
                            albumArtUri = ""
                        }.build())
                        .build())

                // inbox
                add(MediaItem.Builder()
                        .setMetadata(MediaMetadata.Builder().apply {
                            id = MediaID(TYPE_INBOX, null, caller).asString()
                            title = getString(R.string.inbox)
                            albumArtUri = ""
                        }.build())
                        .build())

                // favorites
                add(MediaItem.Builder()
                        .setMetadata(MediaMetadata.Builder().apply {
                            id = MediaID(TYPE_FAVORITE, null, caller).asString()
                            title = getString(R.string.favorites)
                            albumArtUri = ""
                        }.build())
                        .build())

                // history
                add(MediaItem.Builder()
                        .setMetadata(MediaMetadata.Builder().apply {
                            id = MediaID(TYPE_HISTORY, null, caller).asString()
                            title = getString(R.string.history)
                            albumArtUri = ""
                        }.build())
                        .build())
            }
        }


        private fun createMediaItem(mediaId: String): MediaItem {
            return MediaItem.Builder()
                    .setMetadata(
                            MediaMetadata.Builder()
                                    .putString(METADATA_KEY_MEDIA_ID, mediaId)
                                    .putLong(METADATA_KEY_BROWSABLE, BROWSABLE_TYPE_MIXED)
                                    .putLong(METADATA_KEY_PLAYABLE, 1)
                                    .build()
                    )
                    .build()
        }
    }

    private inner class MediaControllerCallBack : MediaController.ControllerCallback() {
        override fun onAllowedCommandsChanged(controller: MediaController, commands: SessionCommandGroup) {
            super.onAllowedCommandsChanged(controller, commands)
        }
    }
}

private const val UAMP_USER_AGENT = "uamp.next"

const val COMMAND_SET_PLAYBACK_RATE = "COMMAND_SET_PLAYBACK_RATE"
const val ARG_PLAYBACK_RATE = "ARG_PLAYBACK_RATE"
