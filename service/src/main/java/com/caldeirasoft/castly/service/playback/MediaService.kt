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

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.*
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.session.MediaButtonReceiver
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.MediaID
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.repository.ItunesRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.service.R
import com.caldeirasoft.castly.service.playback.NotificationBuilder.Companion.NOW_PLAYING_NOTIFICATION
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_EPISODE_ARCHIVE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_EPISODE_TOGGLE_FAVORITE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.EXTRA_EPISODE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.MEDIAID_ROOT
import com.caldeirasoft.castly.service.playback.extensions.id
import com.caldeirasoft.castly.service.playback.extensions.mediaDescription
import com.caldeirasoft.castly.service.playback.extensions.title
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject


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
class MediaService : androidx.media.MediaBrowserServiceCompat() {
    private lateinit var mediaPlaybackSession: MediaPlaybackSession
    private lateinit var becomingNoisyReceiver: BecomingNoisyReceiver
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var notificationBuilder: NotificationBuilder

    private val podcastRepository: PodcastRepository by inject()
    private val episodeRepository: EpisodeRepository by inject()
    private val itunesRepository: ItunesRepository by inject()

    companion object {
        const val EXTRA_CONTINUATION = "android.media.browse.extra.continuation"
        const val EXTRA_SECTION = "android.media.browse.extra.section"
        const val MEDIA_ID_GET_ITEM = "media_id_get_item"
    }


    private var isForegroundService = false


    override fun onCreate() {
        super.onCreate()

        mediaPlaybackSession = MediaPlaybackSession(this, podcastRepository, episodeRepository)
        sessionToken = mediaPlaybackSession.token

        mediaPlaybackSession.controller.registerCallback(ControllerCallback())

        // Sets the notification manager
        notificationBuilder = NotificationBuilder(this)
        notificationManager = NotificationManagerCompat.from(this)
        becomingNoisyReceiver = BecomingNoisyReceiver(context = this, sessionToken = mediaPlaybackSession.token)
    }

    override fun onDestroy() {
        mediaPlaybackSession.release()
    }

    /**
     * Returns the "root" media ID that the client should request to get the list of
     * [MediaItem]s to browse/play.
     */
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return BrowserRoot(MEDIAID_ROOT, null)
    }

    /**
     * Returns (via the [result] parameter) a list of [MediaItem]s that are child
     * items of the provided [parentMediaId]. See [BrowseTree] for more details on
     * how this is build/more details about the relationships.
     */
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaItem>>) {
        onLoadChildren(parentId, result, Bundle().apply {
            putInt(EXTRA_PAGE, 0)
            putInt(EXTRA_PAGE_SIZE, 15)
        })
    }

    /**
     * Returns (via the [result] parameter) a list of [MediaItem]s that are child
     * items of the provided [parentMediaId], with the provided [options].
     */
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaItem>>, options: Bundle) {
        val mediaItems = ArrayList<MediaItem>()
        val mediaIdParent = MediaID().fromString(parentId)
        val mediaType = mediaIdParent.type
        val mediaId = mediaIdParent.id

        when (mediaType) {
            SectionState.ROOT -> {
                mediaItems.addAll(getMediaRoots())
            }
            SectionState.ALL_PODCASTS -> {
                result.detach()
                GlobalScope.launch {
                    podcastRepository.fetchSync().let {
                        mediaItems.addAll(this@MediaService.onGetPodcasts(it))
                    }
                    result.sendResult(mediaItems)
                }
            }
            SectionState.INBOX,
            SectionState.QUEUE,
            SectionState.FAVORITE,
            SectionState.HISTORY -> {
                result.detach()
                GlobalScope.launch {
                    mediaItems.addAll(this@MediaService.onLoadEpisodes(episodeRepository.fetchSync(mediaType.value)))
                    result.sendResult(mediaItems)
                }
            }
            SectionState.PODCAST -> {
                val podcastId = mediaId
                val page = options.getInt(MediaBrowserCompat.EXTRA_PAGE)
                val pageSize = options.getInt(MediaBrowserCompat.EXTRA_PAGE_SIZE)

                result.detach()
                val section = options.getInt(EXTRA_SECTION)
                when (section) {
                    SectionState.QUEUE.value,
                    SectionState.INBOX.value,
                    SectionState.FAVORITE.value,
                    SectionState.HISTORY.value -> {
                        GlobalScope.launch {
                            mediaItems.addAll(onLoadEpisodes(episodeRepository.fetchSync(section, mediaId)))
                            result.sendResult(mediaItems)
                        }
                    }
                    else -> {
                        runBlocking { updateEpisodes(mediaId).await() }
                        mediaItems.addAll(onLoadEpisodes(episodeRepository.fetchSync(mediaId)))
                        result.sendResult(mediaItems)
                    }
                }
            }
            else -> { }
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaPlaybackSession.session, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCustomAction(action: String, extras: Bundle?, result: Result<Bundle>) {
        println("onCustomAction $action")
        val bundleResult = Bundle()
        when (action) {
            COMMAND_CODE_EPISODE_TOGGLE_FAVORITE -> {
                result.detach()
                GlobalScope.launch {
                    extras?.getLong(EXTRA_MEDIA_ID)?.let { mediaId ->
                        episodeRepository.getSync(mediaId)?.let { episode ->
                            episode.isFavorite = !episode.isFavorite
                            episodeRepository.update(episode)
                            val mediaItem = MediaItem(episode.mediaDescription, FLAG_PLAYABLE)
                            bundleResult.putParcelable(EXTRA_EPISODE, mediaItem)

                            result.sendResult(bundleResult)
                        }
                    }
                }
            }

            COMMAND_CODE_EPISODE_ARCHIVE -> {
                result.detach()
                GlobalScope.launch {
                    extras?.getLong(EXTRA_MEDIA_ID)?.let { mediaId ->
                        episodeRepository.getSync(mediaId)?.let { episode ->
                            // archive in database
                            episode.section = SectionState.ARCHIVE.value
                            episodeRepository.update(episode)

                            val mediaItem = MediaItem(episode.mediaDescription, FLAG_PLAYABLE)
                            // remove from queue
                            mediaPlaybackSession.onRemoveQueueItem(mediaItem.description)
                            bundleResult.putParcelable(EXTRA_EPISODE, mediaItem)
                            result.sendResult(bundleResult)
                        }
                    }
                }
            }

            else -> {
                result.detach()
            }
        }
    }

    /**
     * Return podcasts
     */
    private fun onGetPodcasts(podcasts: List<Podcast>): MutableList<MediaItem> {
        return podcasts.map {
            MediaItem(it.mediaDescription, FLAG_BROWSABLE)
        }.toMutableList()
    }

    /**
     * Return episodes
     */
    private fun onLoadEpisodes(episodes: List<Episode>): MutableList<MediaItem> {
        val mediaItems =
                episodes.map { MediaItem(it.mediaDescription, FLAG_PLAYABLE) }.toMutableList()
        return mediaItems
    }

    /**
     * Return episodes MediaItems from a list
     */
    private fun updateEpisodes(podcastId: Long) = GlobalScope.async {
        val bundle = Bundle()
        podcastRepository.getSync(podcastId)?.let { podcastFromDb ->
            // podcast existant en BD : verifier que le nb d'épisodes en BD correspond au trackcount
            // sinon c'est que l'abonnement au podcast s'est fait sans episodes
            val trackCount: Int = episodeRepository.count(podcastId)
            if ((trackCount == 0) && (podcastFromDb.trackCount > 0)) {
                getPodcastWithEpisodesFromItunes(podcastId)?.let { podcastFromItunes ->
                    podcastRepository.update(podcastFromItunes)
                    episodeRepository.insertIgore(podcastFromItunes.episodes)
                }
            }
            else {
                // podcast existant en BD : verifier que la date du derniere episode correspond à la date délivrée par itunes
                // si la date est différente, c'est qu'un nouvel episode est arrivé
                // si c'est le cas, récuperer le podcast et les episodes depuis itunes, sauvegarder les nouveaux episodes en bd
                itunesRepository.lookupAsync(podcastId)?.let { podcastFromLookup ->
                    if (podcastFromDb.releaseDate != podcastFromLookup.releaseDate) {
                        getPodcastWithEpisodesFromItunes(podcastId)?.let { podcastFromItunes ->
                            podcastFromDb.releaseDate = podcastFromItunes.releaseDate
                            podcastRepository.update(podcastFromItunes)
                            episodeRepository.insertIgore(podcastFromItunes.episodes)
                        }
                    }
                }
            }
        }
    }


    /**
     * Return episodes from podcasts
     */
    private suspend fun getPodcastWithEpisodesFromItunes(podcastId: Long) : Podcast? {
        return itunesRepository.getPodcastAsync("143442-3,26", podcastId)
    }


    /**
     * Removes the [NOW_PLAYING_NOTIFICATION] notification.
     *
     * Since `stopForeground(false)` was already called (see
     * [MediaControllerCallback.onPlaybackStateChanged], it's possible to cancel the notification
     * with `notificationManager.cancel(NOW_PLAYING_NOTIFICATION)` if minSdkVersion is >=
     * [Build.VERSION_CODES.LOLLIPOP].
     *
     * Prior to [Build.VERSION_CODES.LOLLIPOP], notifications associated with a foreground
     * service remained marked as "ongoing" even after calling [Service.stopForeground],
     * and cannot be cancelled normally.
     *
     * Fortunately, it's possible to simply call [Service.stopForeground] a second time, this
     * time with `true`. This won't change anything about the service's state, but will simply
     * remove the notification.
     */
    private fun removeNowPlayingNotification() {
        stopForeground(true)
    }

    /**
     * Returns (via the [result] parameter) a list of [MediaItem]s that are child
     * items of [MEDIA_ROOT].
     */
    fun getMediaRoots(): MutableList<MediaItem> {
        return mutableListOf<MediaItem>().apply {
            // podcasts
            add(MediaItem(
                    MediaDescriptionCompat.Builder().apply {
                        id = MediaID(SectionState.ALL_PODCASTS).asString()
                        title = getString(R.string.podcasts)
                    }.build(), FLAG_BROWSABLE))

            // inbox
            add(MediaItem(
                    MediaDescriptionCompat.Builder().apply {
                        id = MediaID(SectionState.INBOX).asString()
                        title = getString(R.string.inbox)
                    }.build(), FLAG_BROWSABLE))

            // favorites
            add(MediaItem(
                    MediaDescriptionCompat.Builder().apply {
                        id = MediaID(SectionState.FAVORITE).asString()
                        title = getString(R.string.favorites)
                    }.build(), FLAG_BROWSABLE))

            // history
            add(MediaItem(
                    MediaDescriptionCompat.Builder().apply {
                        id = MediaID(SectionState.HISTORY).asString()
                        title = getString(R.string.history)
                    }.build(), FLAG_BROWSABLE))
        }
    }

    /**
     * Class to receive callbacks about state changes to the [MediaSessionCompat]. In response
     * to those callbacks, this class:
     *
     * - Build/update the service's notification.
     * - Register/unregister a broadcast receiver for [AudioManager.ACTION_AUDIO_BECOMING_NOISY].
     * - Calls [Service.startForeground] and [Service.stopForeground].
     */
    private inner class ControllerCallback : MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            mediaPlaybackSession.controller.playbackState?.let { updateNotification(it) }
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            state?.let { updateNotification(it) }
        }

        private fun updateNotification(state: PlaybackStateCompat) {
            val updatedState = state.state
            if (mediaPlaybackSession.controller.metadata == null) {
                return
            }

            // Skip building a notification when state is "none".
            val notification = if (updatedState != PlaybackStateCompat.STATE_NONE) {
                notificationBuilder.buildNotification(mediaPlaybackSession.token)
            } else {
                null
            }

            when (updatedState) {
                PlaybackStateCompat.STATE_BUFFERING,
                PlaybackStateCompat.STATE_PLAYING -> {
                    becomingNoisyReceiver.register()

                    /**
                     * This may look strange, but the documentation for [Service.startForeground]
                     * notes that "calling this method does *not* put the service in the started
                     * state itself, even though the name sounds like it."
                     */
                    if (!isForegroundService) {
                        startService(Intent(applicationContext, this@MediaService.javaClass))
                        startForeground(NOW_PLAYING_NOTIFICATION, notification)
                        isForegroundService = true
                    } else if (notification != null) {
                        notificationManager.notify(NOW_PLAYING_NOTIFICATION, notification)
                    }
                }
                else -> {
                    becomingNoisyReceiver.unregister()

                    if (isForegroundService) {
                        stopForeground(false)
                        isForegroundService = false

                        // If playback has ended, also stop the service.
                        if (updatedState == PlaybackStateCompat.STATE_NONE) {
                            stopSelf()
                        }

                        if (notification != null) {
                            notificationManager.notify(NOW_PLAYING_NOTIFICATION, notification)
                        } else {
                            removeNowPlayingNotification()
                        }
                    }
                }
            }
        }
    }

    /**
     * Helper class for listening for when headphones are unplugged (or the audio
     * will otherwise cause playback to become "noisy").
     */
    private class BecomingNoisyReceiver(private val context: Context,
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


    //endregion

}