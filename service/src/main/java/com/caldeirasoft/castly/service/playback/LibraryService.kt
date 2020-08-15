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
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat.EXTRA_MEDIA_ID
import androidx.media.AudioAttributesCompat
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.common.MediaMetadata.*
import androidx.media2.common.UriMediaItem
import androidx.media2.exoplayer.external.C
import androidx.media2.exoplayer.external.audio.AudioAttributes
import androidx.media2.player.MediaPlayer
import androidx.media2.session.*
import androidx.media2.session.SessionResult.RESULT_SUCCESS
import com.caldeirasoft.castly.domain.model.entities.Episode
import com.caldeirasoft.castly.domain.model.entities.MediaID
import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.domain.model.entities.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.repository.ItunesRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.service.R
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_EPISODE_ARCHIVE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_EPISODE_TOGGLE_FAVORITE
import com.caldeirasoft.castly.service.playback.extensions.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import java.util.concurrent.Executors
import kotlin.random.Random


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
class LibraryService : MediaLibraryService() {
    private lateinit var mediaSession: MediaLibrarySession
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var controllerInfo: MediaSession.ControllerInfo
    private var id = ""

    private val podcastRepository: PodcastRepository by inject()
    private val episodeRepository: EpisodeRepository by inject()
    private val itunesRepository: ItunesRepository by inject()

    companion object {
        const val ID = "PODCAST_LIBRARY"
        const val EXTRA_CONTINUATION = "android.media.browse.extra.continuation"
        const val EXTRA_SECTION = "android.media.browse.extra.section"
        const val MEDIA_ID_GET_ITEM = "media_id_get_item"
    }


    private var isForegroundService = false

    private val ROOT_ITEM: MediaItem
    private val ROOT_PARAMS: LibraryParams

    init {
        ROOT_ITEM = MediaItem.Builder().setMetadata(
                MediaMetadata.Builder().apply {
                    id = MediaID(SectionState.ROOT).asString()
                    browsable = BROWSABLE_TYPE_MIXED
                    playable = 0
                }.build()).build()
        ROOT_PARAMS = LibraryParams.Builder()
                .setExtras(Bundle().apply {
                    putString(ID, ID)
                }).build()
    }

    private val uAmpAudioAttributes = AudioAttributes.Builder()
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

    override fun onCreate() {
        super.onCreate()
        createPlayer()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        this.controllerInfo = controllerInfo
        return mediaSession
    }

    /**
     * Create media player
     */
    private fun createPlayer() {
        mediaPlayer = MediaPlayer(this)
        createMediaSession()
    }

    /**
     * Create media session
     */
    private fun createMediaSession() {
        id = Random.nextInt().toString() + System.currentTimeMillis()
        mediaSession = MediaLibrarySession.Builder(this
                , mediaPlayer
                , Executors.newSingleThreadExecutor()
                , SessionCallback())
                .setId(id)
                .build()
    }

    private inner class SessionCallback : MediaLibrarySession.MediaLibrarySessionCallback() {

        override fun onConnect(session: MediaSession, controller: MediaSession.ControllerInfo): SessionCommandGroup? {
            mediaPlayer.setAudioAttributes(
                    AudioAttributesCompat.Builder()
                            .setContentType(AudioAttributesCompat.CONTENT_TYPE_SPEECH)
                            .setUsage(AudioAttributesCompat.USAGE_MEDIA)
                            .build())

            //mediaPlayer.prepare()
            return super.onConnect(session, controller)
        }

        override fun onGetLibraryRoot(session: MediaLibrarySession, controller: MediaSession.ControllerInfo, params: LibraryParams?): LibraryResult {
            return LibraryResult(LibraryResult.RESULT_SUCCESS, ROOT_ITEM, ROOT_PARAMS)
        }

        override fun onSubscribe(session: MediaLibrarySession, controller: MediaSession.ControllerInfo, parentId: String, params: LibraryParams?): Int {
            val mediaIdParent = MediaID().fromString(parentId)
            val mediaType = mediaIdParent.type
            val mediaId = mediaIdParent.id
            val list = when (mediaType) {
                SectionState.ALL_PODCASTS -> {
                    podcastRepository.fetchSync()
                }
                SectionState.INBOX,
                SectionState.QUEUE,
                SectionState.FAVORITE,
                SectionState.HISTORY -> {
                    episodeRepository.fetchSync(mediaType.value)
                }
                SectionState.PODCAST -> {
                    episodeRepository.fetchSync(mediaId)
                }
                else -> arrayListOf()
            }

            session.notifyChildrenChanged(controller, parentId, list.size, params)
            return LibraryResult.RESULT_SUCCESS
        }

        override fun onUnsubscribe(session: MediaLibrarySession, controller: MediaSession.ControllerInfo, parentId: String): Int {
            return LibraryResult.RESULT_SUCCESS
        }

        override fun onCommandRequest(session: MediaSession, controller: MediaSession.ControllerInfo, command: SessionCommand): Int {
            when(command.customAction) {
                COMMAND_CODE_EPISODE_TOGGLE_FAVORITE -> {
                    GlobalScope.launch {
                        command.customExtras?.getLong(EXTRA_MEDIA_ID)?.let { mediaId ->
                            episodeRepository.getSync(mediaId)?.let { episode ->
                                episode.isFavorite = !episode.isFavorite
                                episodeRepository.update(episode)
                            }
                        }
                    }
                    return RESULT_SUCCESS
                }

                COMMAND_CODE_EPISODE_ARCHIVE -> {
                    GlobalScope.launch {
                        command.customExtras?.getLong(EXTRA_MEDIA_ID)?.let { mediaId ->
                            episodeRepository.getSync(mediaId)?.let { episode ->
                                // archive in database
                                episode.section = SectionState.ARCHIVE.value
                                episodeRepository.update(episode)
                            }
                        }
                    }
                    return RESULT_SUCCESS
                }
            }
            return RESULT_SUCCESS
        }

        override fun onCreateMediaItem(session: MediaSession, controller: MediaSession.ControllerInfo, mediaId: String): MediaItem? {
            //val episode = episodeRepository.getSync(mediaId.toLong())
            //val mediaItem: MediaItem? = episode?.let { episode ->  MediaItem.Builder().setMetadata(episode.mediaMetadata).build() }
            //val mediaItem: FileMediaItem
            val uri = "http://content.blubrry.com/codingwithmitch/Justin_Mitchel_interview_audio_online-audio-converter.com_.mp3"
            val mediaItem: MediaItem = UriMediaItem.Builder(Uri.parse(uri))
                    .setMetadata(MediaMetadata.Builder()
                            .putString(METADATA_KEY_MEDIA_ID, mediaId)
                            .putText(METADATA_KEY_DISPLAY_TITLE,"Justin Mitchel Interview")
                            .putText(METADATA_KEY_ARTIST, "Coding With Mitch")
                            .putText(METADATA_KEY_ALBUM, "Coding With Mitch")
                            .putText(METADATA_KEY_MEDIA_URI, uri)
                            .putLong(METADATA_KEY_DURATION, 0)
                            .putLong(METADATA_KEY_BROWSABLE, BROWSABLE_TYPE_NONE)
                            .putLong(METADATA_KEY_PLAYABLE, 1)
                            .build())
                    .build()
            return mediaItem
        }

        override fun onGetItem(session: MediaLibrarySession, controller: MediaSession.ControllerInfo, mediaId: String): LibraryResult {
            return if (mediaId == MEDIA_ID_GET_ITEM) {
                LibraryResult(LibraryResult.RESULT_SUCCESS, createMediaItem(mediaId), null)
            } else {
                LibraryResult(LibraryResult.RESULT_ERROR_BAD_VALUE)
            }
        }

        override fun onGetChildren(session: MediaLibrarySession,
                                   controller: MediaSession.ControllerInfo,
                                   parentId: String,
                                   page: Int,
                                   pageSize: Int,
                                   params: LibraryParams?): LibraryResult {
            val mediaItems = ArrayList<MediaItem>()
            val mediaIdParent = MediaID().fromString(parentId)
            val mediaType = mediaIdParent.type
            val mediaId = mediaIdParent.id

            when (mediaType) {
                SectionState.ROOT -> {
                    addMediaRoots(mediaItems)
                    return LibraryResult(LibraryResult.RESULT_SUCCESS, mediaItems, null)
                }
                SectionState.ALL_PODCASTS -> {
                    podcastRepository.fetchSync().let {
                        return onGetPodcasts(it)
                    }
                }
                SectionState.INBOX,
                SectionState.QUEUE,
                SectionState.FAVORITE,
                SectionState.HISTORY -> {
                    return onLoadEpisodes(episodeRepository.fetchSync(mediaType.value))
                }
                SectionState.PODCAST -> {
                    val podcastUrl = mediaId

                    val section = params?.extras?.getInt(EXTRA_SECTION)
                    when (section) {
                        SectionState.QUEUE.value,
                        SectionState.INBOX.value,
                        SectionState.FAVORITE.value,
                        SectionState.HISTORY.value -> {
                            return onLoadEpisodes(episodeRepository.fetchSync(section, podcastUrl))
                        }
                        else -> {
                            runBlocking { updateEpisodes(mediaId).await() }
                            return onLoadEpisodes(episodeRepository.fetchSync(mediaId))
                        }
                    }
                }
                else -> {
                    return LibraryResult(LibraryResult.RESULT_ERROR_BAD_VALUE)
                }
            }
        }

        fun onGetPodcasts(podcasts: List<Podcast>): LibraryResult {
            val mediaItems = podcasts.map {
                MediaItem.Builder()
                        .setMetadata(it.mediaMetadata)
                        .build() }
            return LibraryResult(LibraryResult.RESULT_SUCCESS, mediaItems, null)
        }

        private fun onLoadEpisodes(episodes: List<Episode>): LibraryResult {
            val mediaItems = episodes.map { MediaItem.Builder().setMetadata(it.mediaMetadata).build() }
            return LibraryResult(LibraryResult.RESULT_SUCCESS, mediaItems, null)
        }

        fun addMediaRoots(mediaRoot: MutableList<MediaItem>) {
            mediaRoot.apply {
                // podcasts
                add(MediaItem.Builder()
                        .setMetadata(MediaMetadata.Builder().apply {
                            id = MediaID(SectionState.ALL_PODCASTS).asString()
                            title = getString(R.string.podcasts)
                            albumArtUri = ""
                        }.build())
                        .build())

                // inbox
                add(MediaItem.Builder()
                        .setMetadata(MediaMetadata.Builder().apply {
                            id = MediaID(SectionState.INBOX).asString()
                            title = getString(R.string.inbox)
                            albumArtUri = ""
                        }.build())
                        .build())

                // favorites
                add(MediaItem.Builder()
                        .setMetadata(MediaMetadata.Builder().apply {
                            id = MediaID(SectionState.FAVORITE).asString()
                            title = getString(R.string.favorites)
                            albumArtUri = ""
                        }.build())
                        .build())

                // history
                add(MediaItem.Builder()
                        .setMetadata(MediaMetadata.Builder().apply {
                            id = MediaID(SectionState.HISTORY).asString()
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

        /**
         * Return episodes MediaItems from a list
         */
        private fun updateEpisodes(podcastId: Long) = GlobalScope.async {
        }


        private inner class MediaControllerCallBack : MediaController.ControllerCallback() {
            override fun onAllowedCommandsChanged(controller: MediaController, commands: SessionCommandGroup) {
                super.onAllowedCommandsChanged(controller, commands)
            }
        }

        override fun onFastForward(session: MediaSession, controller: MediaSession.ControllerInfo): Int {
            return super.onFastForward(session, controller)
        }

        override fun onRewind(session: MediaSession, controller: MediaSession.ControllerInfo): Int {
            return super.onRewind(session, controller)
        }

        override fun onSkipBackward(session: MediaSession, controller: MediaSession.ControllerInfo): Int {
            return super.onSkipBackward(session, controller)
        }

        override fun onSkipForward(session: MediaSession, controller: MediaSession.ControllerInfo): Int {
            return super.onSkipForward(session, controller)
        }

        override fun onPlayFromMediaId(session: MediaSession, controller: MediaSession.ControllerInfo, mediaId: String, extras: Bundle?): Int {
            return super.onPlayFromMediaId(session, controller, mediaId, extras)
        }
    }


    //endregion

}