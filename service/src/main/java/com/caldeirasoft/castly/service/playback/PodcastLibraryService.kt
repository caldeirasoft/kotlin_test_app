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
import android.app.PendingIntent
import android.os.Bundle
import android.os.Process
import android.util.Log
import androidx.media2.*
import androidx.media2.MediaController.ControllerResult.RESULT_CODE_BAD_VALUE
import androidx.media2.MediaController.ControllerResult.RESULT_CODE_SUCCESS
import androidx.media2.MediaMetadata.*
import androidx.media2.exoplayer.external.C
import androidx.media2.exoplayer.external.ExoPlayer
import androidx.media2.exoplayer.external.ExoPlayerFactory
import androidx.media2.exoplayer.external.audio.AudioAttributes
import androidx.versionedparcelable.ParcelUtils
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.MediaID
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.repository.FeedlyRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.service.R
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_PODCAST_GET_DESCRIPTION
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_PODCAST_REFRESH
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_PODCAST_SUBSCRIBE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_PODCAST_UNSUBSCRIBE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.METADATA_KEY_IN_DATABASE
import com.caldeirasoft.castly.service.playback.extensions.*
import org.koin.android.ext.android.inject
import java.io.InputStream
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
class PodcastLibraryService : LifecycleMediaBrowserService() {
    private lateinit var mediaSession: MediaLibrarySession

    private val podcastRepository: PodcastRepository by inject()
    private val episodeRepository: EpisodeRepository by inject()
    private val feedlyRepository: FeedlyRepository by inject()
    private val mediaItemsHashMap: HashMap<String, MediaItem> = hashMapOf()

    companion object {
        const val ID = "TEST"

        const val EXTRA_MEDIA_ID = "android.media.browse.extra.media_id"
        const val EXTRA_PODCAST = "android.media.browse.extra.podcast"
        const val EXTRA_CONTINUATION = "android.media.browse.extra.continuation"
        const val EXTRA_SECTION = "android.media.browse.extra.section"
        const val EXTRA_RELOAD_ALL = "android.media.browse.extra.reload"

        const val NOTIFICATION_ID = 888

        const val MEDIA_ID_GET_ITEM = "media_id_get_item"

        val ROOT_ITEM: MediaItem
        val ROOT_PARAMS: LibraryParams

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

    override fun onUpdateNotification(session: MediaSession): MediaNotification? {
        return super.onUpdateNotification(session)
    }

    override fun onGetSession(): MediaLibrarySession {
        // Build a PendingIntent that can be used to launch the UI.
        val sessionIntent = packageManager?.getLaunchIntentForPackage(packageName)
        val sessionActivityPendingIntent = PendingIntent.getActivity(this, 0, sessionIntent, 0)

        // create a new media player
        val mediaPlayer = MediaPlayer(this)
        val ioExecutor = Executors.newSingleThreadExecutor()

        val sessionCallback = SessionCallback()
        // Create a new MediaSession.
        mediaSession = MediaLibrarySession.Builder(this,
                mediaPlayer,
                ioExecutor,
                sessionCallback)
                .setId(ID + System.currentTimeMillis())
                .build()

        return mediaSession
    }

    private inner class SessionCallback : MediaLibrarySession.MediaLibrarySessionCallback() {
        // Episodes loaded Pages
        private val loadedPages = hashMapOf<Int, String>()
        // Episodes backend
        private val mediaItemsBackstore: MutableList<MediaItem> = arrayListOf()
        // Continuation
        private val continuation: String? = ""

        override fun onGetLibraryRoot(session: MediaLibrarySession, controller: MediaSession.ControllerInfo, params: LibraryParams?): LibraryResult {
            return LibraryResult(RESULT_CODE_SUCCESS, ROOT_ITEM, ROOT_PARAMS)
        }

        override fun onSubscribe(session: MediaLibrarySession, controller: MediaSession.ControllerInfo, parentId: String, params: LibraryParams?): Int {
            if (Process.myUid() == controller.uid) {
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
            }
            return RESULT_CODE_SUCCESS
        }

        override fun onUnsubscribe(session: MediaLibrarySession, controller: MediaSession.ControllerInfo, parentId: String): Int {
            return RESULT_CODE_SUCCESS
        }

        override fun onCustomCommand(session: MediaSession, controller: MediaSession.ControllerInfo, customCommand: SessionCommand, args: Bundle?): MediaSession.SessionResult {
            when (customCommand.customCommand) {
                COMMAND_CODE_PODCAST_SUBSCRIBE -> {
                    val mediaItemPodcast = ParcelUtils.getVersionedParcelable<MediaItem>(args, EXTRA_PODCAST)
                    mediaItemPodcast?.let {
                        subscribeToPodcast(mediaItem = it)
                        val bundleResult = Bundle().apply {
                            ParcelUtils.putVersionedParcelable(this, EXTRA_PODCAST, it)
                        }

                        val controllers = (session as MediaLibrarySession).connectedControllers

                        //session
                        session as MediaLibrarySession
                        Log.d("notifyChildrenChanged", MediaID(SectionState.ALL_PODCASTS).asString())
                        session.notifyChildrenChanged(MediaID(SectionState.ALL_PODCASTS).asString(), 1, LibraryParams.Builder().build())

                        Log.d("notifyChildrenChanged", it.mediaId.orEmpty())
                        session.notifyChildrenChanged(it.mediaId.orEmpty(), 1, LibraryParams.Builder().build())

                        //super.onCustomCommand(session, controller, customCommand, args)
                        return MediaSession.SessionResult(RESULT_CODE_SUCCESS, bundleResult)
                    }
                }

                COMMAND_CODE_PODCAST_UNSUBSCRIBE -> {
                    val mediaItemPodcast = ParcelUtils.getVersionedParcelable<MediaItem>(args, EXTRA_PODCAST)
                    mediaItemPodcast?.let {
                        unsubscribeFromPodcast(mediaItem = it)
                        val bundleResult = Bundle().apply {
                            ParcelUtils.putVersionedParcelable(this, EXTRA_PODCAST, it)
                        }

                        //session.broadcastCustomCommand()

                        session as MediaLibrarySession

                        Log.d("notifyChildrenChanged", MediaID(SectionState.ALL_PODCASTS).asString())
                        session.notifyChildrenChanged(MediaID(SectionState.ALL_PODCASTS).asString(), 1, LibraryParams.Builder().build())

                        Log.d("notifyChildrenChanged", it.mediaId.orEmpty())
                        session.notifyChildrenChanged(it.mediaId.orEmpty(), 1, LibraryParams.Builder().build())

                        //super.onCustomCommand(session, controller, customCommand, args)
                        return MediaSession.SessionResult(RESULT_CODE_SUCCESS, bundleResult)
                    }
                }

                COMMAND_CODE_PODCAST_GET_DESCRIPTION -> {
                    val mediaItemPodcast = ParcelUtils.getVersionedParcelable<MediaItem>(args, EXTRA_PODCAST)
                    mediaItemPodcast?.let {
                        getPodcastInfoFromDb(mediaItem = it)
                        getPodcastDescription(mediaItem = it)
                        val bundleResult = Bundle().apply {
                            ParcelUtils.putVersionedParcelable(this, EXTRA_PODCAST, it)
                        }

                        return MediaSession.SessionResult(RESULT_CODE_SUCCESS, bundleResult)
                    }

                    val mediaId = args?.getString(EXTRA_MEDIA_ID)
                    mediaId?.let {
                        val mediaItem = getPodcastInfoFromDb(mediaId = it)
                        val bundleResult = Bundle().apply {
                            ParcelUtils.putVersionedParcelable(this, EXTRA_PODCAST, mediaItem)
                        }
                        return MediaSession.SessionResult(RESULT_CODE_SUCCESS, bundleResult)
                    }
                }

                COMMAND_CODE_PODCAST_REFRESH -> {
                    val mediaId = args?.getString(EXTRA_MEDIA_ID)
                    mediaId?.let {
                        session as MediaLibrarySession
                        session.notifyChildrenChanged(mediaId, 0, LibraryParams.Builder().build())
                        return MediaSession.SessionResult(RESULT_CODE_SUCCESS, Bundle())
                    }
                }
            }

            return MediaSession.SessionResult(RESULT_CODE_BAD_VALUE, null)
        }

        override fun onCreateMediaItem(session: MediaSession, controller: MediaSession.ControllerInfo, mediaId: String): MediaItem? {
            return mediaItemsHashMap.get(mediaId)
        }

        override fun onGetItem(session: MediaLibrarySession, controller: MediaSession.ControllerInfo, mediaId: String): LibraryResult {
            return if (mediaId == MEDIA_ID_GET_ITEM) {
                LibraryResult(RESULT_CODE_SUCCESS, createMediaItem(mediaId), null)
            } else {
                LibraryResult(RESULT_CODE_BAD_VALUE)
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
                    return LibraryResult(RESULT_CODE_SUCCESS, mediaItems, null)
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
                    val reloadAll = params?.extras?.getBoolean(EXTRA_RELOAD_ALL)

                    if (reloadAll != true) {
                        if (page == 0)
                            mediaItemsBackstore.clear()

                        val section = params?.extras?.getInt(EXTRA_SECTION)
                        when (section) {
                            SectionState.QUEUE.value,
                            SectionState.INBOX.value,
                            SectionState.FAVORITE.value,
                            SectionState.HISTORY.value -> {
                                return onLoadEpisodes(episodeRepository.fetchSync(section, podcastUrl), true)
                            }
                            else -> {
                                val continuation = loadedPages.get(page - 1)
                                var episodes: List<Episode> = arrayListOf()
                                episodes = onLoadEpisodesFromFeedly(podcastUrl, params, page, pageSize, continuation)
                                return onLoadEpisodes(episodes, true)
                            }
                        }
                    } else {
                        val episodesFromDb = episodeRepository.fetchSync(podcastUrl)
                        return LibraryResult(RESULT_CODE_SUCCESS, mediaItemsBackstore, null)
                    }
                }
                else -> {
                    return LibraryResult(RESULT_CODE_BAD_VALUE)
                }
            }
        }

        fun onGetPodcasts(podcasts: List<Podcast>): LibraryResult {
            val mediaItems = podcasts.map {
                MediaItem.Builder()
                        .setMetadata(it.getMediaMetadata(true))
                        .build() }
            return LibraryResult(RESULT_CODE_SUCCESS, mediaItems, null)
        }

        private fun onLoadEpisodes(episodes: List<Episode>, addToBackstore: Boolean = false): LibraryResult {
            val mediaItems = episodes.map { MediaItem.Builder().setMetadata(it.mediaMetadata).build() }
            if (addToBackstore)
                this.mediaItemsBackstore.addAll(mediaItems)
            mediaItems.forEach {
                mediaItemsHashMap.put(it.mediaId.toString(), it)
            }
            return LibraryResult(RESULT_CODE_SUCCESS, mediaItems, null)
        }

        private fun onLoadEpisodesFromFeedly(
                podcastUrl: String,
                params: LibraryParams?,
                page: Int,
                pageSize: Int,
                continuation: String?,
                alreadyRetrievedEpisodes: MutableList<Episode> = java.util.ArrayList()): List<Episode> {

            val episodes: MutableList<Episode> = arrayListOf()
            val podcast =
                    ParcelUtils.getVersionedParcelable<MediaItem>(params?.extras, EXTRA_PODCAST)?.toPodcast()
                            ?: podcastRepository.getSync(podcastUrl)
            podcast?.let {
                episodes.addAll(onLoadEpisodesFromFeedly(podcast, page, pageSize, continuation, alreadyRetrievedEpisodes))
            }
            return episodes
        }

        private fun onLoadEpisodesFromFeedly(
                podcast: Podcast,
                page: Int,
                pageSize: Int,
                continuation: String?,
                alreadyRetrievedEpisodes: MutableList<Episode> = java.util.ArrayList()): List<Episode> {

            val episodes: MutableList<Episode> = arrayListOf()
            val responseEntries = feedlyRepository.getStreamEntries(podcast, pageSize, continuation.orEmpty())
            responseEntries
                    ?.apply { data.forEach { episode -> retrieveEpisodeDataFromDb(episode) }}
                    ?.let {
                        alreadyRetrievedEpisodes.addAll(it.data)
                        if (!it.continuation.isNullOrEmpty() &&
                                (it.data.size < pageSize - alreadyRetrievedEpisodes.size)) {
                            episodes.addAll(onLoadEpisodesFromFeedly(podcast, page, pageSize - alreadyRetrievedEpisodes.size, it.continuation, alreadyRetrievedEpisodes))
                        } else {
                            loadedPages.put(page, it.continuation.orEmpty())
                            episodes.addAll(alreadyRetrievedEpisodes)
                        }
                    }
            return episodes
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
         * Subscribe to a new podcast
         */
        private fun subscribeToPodcast(mediaItem: MediaItem): Boolean {
            // get podcast info
            val metadata = mediaItem.metadata
            val podcastUrl = MediaID().fromString(mediaItem.mediaId.orEmpty()).id
            podcastUrl.let { url ->
                feedlyRepository
                        .getPodcastFromFeedlyApi(url)
                        ?.let {
                            // get date and description
                            val newMetadata = MediaMetadata.Builder(metadata!!).apply {
                                this.displayDescription = it.description
                                this.date = it.updated.toString()
                                this.setExtras(Bundle())
                            }.build()
                            mediaItem.metadata = newMetadata
                            val podcast = mediaItem.toPodcast()
                            podcastRepository.insert(podcast)

                            // update metadata
                            Log.d("MetaData extras", mediaItem.metadata?.extras.toString())
                            mediaItem.metadata?.extras?.putBoolean(METADATA_KEY_IN_DATABASE, true)

                            // get last episode
                            feedlyRepository.getLastEpisode(podcast)
                                    ?.apply {
                                        this.section = SectionState.INBOX.value
                                        episodeRepository.insert(this)
                                    }
                        }
            }
            return true
        }

        /**
         * Unsubscribe from podcast
         */
        private fun unsubscribeFromPodcast(mediaItem: MediaItem) {
            val podcast = mediaItem.toPodcast()

            // delete podcast
            podcastRepository.delete(podcast)

            // delete episodes
            episodeRepository.deleteByPodcast(podcast.feedUrl)

            //
            mediaItem.metadata?.extras?.remove(METADATA_KEY_IN_DATABASE)
        }

        /**
         * Get description of a podcast
         */
        private fun getPodcastInfoFromDb(mediaItem: MediaItem){
            // get podcast info
            val podcastUrl = MediaID.fromString(mediaItem.mediaId).id
            podcastRepository.getSync(podcastUrl)?.let { podcast ->
                val newMetadata = MediaMetadata
                        .Builder(podcast
                                .getMediaMetadata(true))
                        .build()
                mediaItem.metadata = newMetadata
            }
        }

        /**
         * Get description of a podcast
         */
        private fun getPodcastInfoFromDb(mediaId: String): MediaItem {
            // get podcast info
            val podcastUrl = MediaID.fromString(mediaId).id
            return MediaItem.Builder().apply {
                podcastRepository.getSync(podcastUrl)?.let { podcast ->
                    setMetadata(podcast.getMediaMetadata(true))
                }
            }.build()
        }

        /**
         * Get description of a podcast
         */
        private fun getPodcastDescription(mediaItem: MediaItem): Boolean {
            // get podcast info
            val metadata = mediaItem.metadata
            if (metadata?.displayDescription == null) {
                MediaID.fromString(mediaItem.mediaId).id.let { url ->
                    feedlyRepository
                            .getPodcastFromFeedlyApi(url)
                            ?.let {
                                val newMetadata = MediaMetadata.Builder(metadata!!).apply {
                                    this.displayDescription = it.description
                                    this.date = it.updated.toString()
                                }.build()
                                mediaItem.metadata = newMetadata
                            }
                }
            }
            return true
        }

        /**
         * Get episodes info from DB
         */
        private fun retrieveEpisodeDataFromDb(episode: Episode) {
            // get episode in db
            val episodeInDb: Episode? = episodeRepository.getSync(episode.episodeId)
            episodeInDb.apply {
                this?.apply {
                    // get value from db
                    episode.section = this.section
                    episode.isFavorite = this.isFavorite
                    episode.duration = this.duration
                    episode.playbackPosition = this.playbackPosition
                    episode.queuePosition = this.queuePosition
                }
            } ?: run {
                // if the episode is new / not in db : get from podcast
                // get value from podcast
                episode.section = SectionState.ARCHIVE.value
                //retrieveEpisodeDuration(episode)
            }
        }

        private inner class MediaControllerCallBack : MediaController.ControllerCallback() {
            override fun onAllowedCommandsChanged(controller: MediaController, commands: SessionCommandGroup) {
                super.onAllowedCommandsChanged(controller, commands)
            }
        }
    }
}