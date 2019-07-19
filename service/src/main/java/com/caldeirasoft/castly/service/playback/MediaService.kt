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
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.media.session.MediaButtonReceiver
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.MediaID
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.repository.FeedlyRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.service.R
import com.caldeirasoft.castly.service.playback.NotificationBuilder.Companion.NOW_PLAYING_NOTIFICATION
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_EPISODE_ARCHIVE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_EPISODE_TOGGLE_FAVORITE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_PODCAST_GET_DESCRIPTION
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_PODCAST_SUBSCRIBE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_PODCAST_UNSUBSCRIBE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.EXTRA_EPISODE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.EXTRA_PODCAST
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.MEDIA_ROOT
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.STATUS_NOT_IN_DATABASE
import com.caldeirasoft.castly.service.playback.extensions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private val feedlyRepository: FeedlyRepository by inject()

    companion object {
        const val EXTRA_CONTINUATION = "android.media.browse.extra.continuation"
        const val EXTRA_SECTION = "android.media.browse.extra.section"
        const val MEDIA_ID_GET_ITEM = "media_id_get_item"
    }


    private var isForegroundService = false


    override fun onCreate() {
        super.onCreate()

        mediaPlaybackSession = MediaPlaybackSession(this, podcastRepository, episodeRepository, feedlyRepository)
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
        return BrowserRoot(MEDIA_ROOT, null)
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
                val podcastUrl = mediaId
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
                            mediaItems.addAll(onLoadEpisodes(episodeRepository.fetchSync(section, podcastUrl)))
                            result.sendResult(mediaItems)
                        }
                    }
                    else -> {
                        onLoadEpisodesFromFeedly(podcastUrl, options, page, pageSize, result)
                    }
                }

            }
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
            COMMAND_CODE_PODCAST_SUBSCRIBE -> {
                result.detach()
                val mediaItemPodcast = extras?.getParcelable<MediaItem>(EXTRA_PODCAST)
                mediaItemPodcast?.let {
                    GlobalScope.launch {
                        subscribeToPodcast(mediaItem = it).let {
                            bundleResult.putParcelable(EXTRA_PODCAST, it)

                            // notify children changed
                            Log.d("notifyChildrenChanged", MediaID(SectionState.ALL_PODCASTS).asString())
                            notifyChildrenChanged(MediaID(SectionState.ALL_PODCASTS).asString())

                            // notify children changed
                            Log.d("notifyChildrenChanged", it.mediaId.orEmpty())
                            notifyChildrenChanged(it.mediaId.orEmpty())

                            result.sendResult(bundleResult)
                        }
                    }
                }
            }

            COMMAND_CODE_PODCAST_UNSUBSCRIBE -> {
                result.detach()
                val mediaItemPodcast = extras?.getParcelable<MediaItem>(EXTRA_PODCAST)
                mediaItemPodcast?.let {
                    GlobalScope.launch {
                        unsubscribeFromPodcast(mediaItem = it)
                        bundleResult.putParcelable(EXTRA_PODCAST, it)

                        // notify children changed
                        Log.d("notifyChildrenChanged", MediaID(SectionState.ALL_PODCASTS).asString())
                        notifyChildrenChanged(MediaID(SectionState.ALL_PODCASTS).asString())

                        // notify children changed
                        Log.d("notifyChildrenChanged", it.mediaId.orEmpty())
                        notifyChildrenChanged(it.mediaId.orEmpty())

                        //mediaPlaybackSession.session.sendSessionEvent()

                        result.sendResult(bundleResult)
                    }
                }
            }

            COMMAND_CODE_PODCAST_GET_DESCRIPTION -> {
                result.detach()
                GlobalScope.launch {
                    val mediaItemPodcast = extras?.getParcelable<MediaItem>(EXTRA_PODCAST)
                    val mediaId = extras?.getString(EXTRA_MEDIA_ID)
                    if (mediaItemPodcast != null) {
                        getPodcastInfoFromDb(mediaItem = mediaItemPodcast)
                                ?.let { mediaItem ->
                                    bundleResult.putParcelable(EXTRA_PODCAST, mediaItem) }
                                ?: getPodcastDescription(mediaItem = mediaItemPodcast)
                                        .let { mediaItem ->
                                            bundleResult.putParcelable(EXTRA_PODCAST, mediaItem)
                                        }
                        result.sendResult(bundleResult)
                    }


                    else if (mediaId != null) {
                        getPodcastInfoFromDb(mediaId = mediaId)?.let { mediaItem ->
                            bundleResult.putParcelable(EXTRA_PODCAST, mediaItem)
                        }
                        result.sendResult(bundleResult)
                    }
                }
            }

            COMMAND_CODE_EPISODE_TOGGLE_FAVORITE -> {
                result.detach()
                GlobalScope.launch {
                    extras?.getString(EXTRA_MEDIA_ID)?.let { mediaId ->
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
                    extras?.getString(EXTRA_MEDIA_ID)?.let { mediaId ->
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
        val mediaItems = podcasts.map {
            MediaItem(it.asMediaDescription(true), FLAG_BROWSABLE)
        }.toMutableList()
        return mediaItems
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
     * Return episodes from feedly
     */
    private fun onLoadEpisodesFromFeedly(
            podcastUrl: String,
            params: Bundle,
            page: Int,
            pageSize: Int,
            result: Result<MutableList<MediaItem>>)
    {
        GlobalScope.launch {
            val episodes: MutableList<Episode> = arrayListOf()
            val alreadyRetrievedEpisodes: MutableList<Episode> = arrayListOf()
            val podcast = params.getParcelable<MediaItem>(EXTRA_PODCAST)?.toPodcast()
                    ?: podcastRepository.getSync(podcastUrl)
            val continuation = params.getString(EXTRA_CONTINUATION)
            podcast?.let {
                onLoadEpisodesFromFeedlyWithContinuation(it, params, page, pageSize, continuation, alreadyRetrievedEpisodes).let { list ->
                    episodes.addAll(list)
                    result.sendResult(onLoadEpisodes(episodes))
                }
            }
        }
    }

    /**
     * Get episodes from feedly
     */
    private suspend fun onLoadEpisodesFromFeedlyWithContinuation(
            podcast: Podcast,
            params: Bundle,
            page: Int,
            pageSize: Int,
            continuation: String?,
            alreadyRetrievedEpisodes: MutableList<Episode> = java.util.ArrayList()): List<Episode> {

        val episodes: MutableList<Episode> = arrayListOf()
        val responseEntries = feedlyRepository.getStreamEntries(podcast, pageSize, continuation.orEmpty())
        responseEntries
                .apply { data.forEach { episode -> retrieveEpisodeDataFromDb(episode) }}
                .let {
                    alreadyRetrievedEpisodes.addAll(it.data)
                    if (!it.continuation.isNullOrEmpty() &&
                            (it.data.size < pageSize - alreadyRetrievedEpisodes.size)) {
                        episodes.addAll(onLoadEpisodesFromFeedlyWithContinuation(podcast, params, page, pageSize - alreadyRetrievedEpisodes.size, it.continuation, alreadyRetrievedEpisodes))
                    } else {
                        params.putString(EXTRA_CONTINUATION, it.continuation)
                        episodes.addAll(alreadyRetrievedEpisodes)
                    }
                }
        return episodes
    }

    /**
     * Subscribe to a new podcast
     */
    private suspend fun subscribeToPodcast(mediaItem: MediaItem): MediaItem {
        // get podcast info
        val podcastUrl = MediaID().fromString(mediaItem.mediaId.orEmpty()).id
        podcastUrl.let { url ->
            feedlyRepository
                    .getPodcastFromFeedlyApi(url)
                    ?.let {
                        // mediaItem url
                        it.title = mediaItem.description.title.toString()
                        it.authors = mediaItem.description.artist
                        it.imageUrl = mediaItem.description.albumArtUri.toString()

                        // insert podcast
                        podcastRepository.insert(it)

                        // update metadata
                        val newMediaItem = it.asMediaItem(true)

                        // get last episode
                        feedlyRepository.getLastEpisode(it)
                                ?.apply {
                                    this.section = SectionState.INBOX.value
                                    episodeRepository.insert(this)
                                }

                        return newMediaItem
                    }
        }

        return mediaItem
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

        // remove database tag
        mediaItem.description.inDatabaseStatus = STATUS_NOT_IN_DATABASE
    }

    /**
     * Get description of a podcast
     */
    private fun getPodcastInfoFromDb(mediaItem: MediaItem): MediaItem? {
        // get podcast info
        return getPodcastInfoFromDb(mediaItem.mediaId!!)?.let { return it }
    }

    /**
     * Get description of a podcast
     */
    private fun getPodcastInfoFromDb(mediaId: String): MediaItem? {
        // get podcast info
        val podcastUrl = MediaID.fromString(mediaId).id
        return podcastRepository.getSync(podcastUrl)?.let {podcast ->
            MediaItem(podcast.asMediaDescription(true), FLAG_BROWSABLE)
        }
    }

    /**
     * Get description of a podcast
     */
    private suspend fun getPodcastDescription(mediaItem: MediaItem): MediaItem {
        // get podcast info
        val mediaDescription = mediaItem.description
        if (mediaDescription.description == null) {
            MediaID.fromString(mediaItem.mediaId).id.let { url ->
                feedlyRepository
                        .getPodcastFromFeedlyApi(url)
                        ?.let { podcast ->
                            val newMediaDescription =
                                    MediaMetadataCompat.Builder().also {
                                        it.id = mediaDescription.mediaId.orEmpty()
                                        it.title = mediaDescription.title.toString()
                                        it.artist = mediaDescription.artist
                                        it.displayTitle = mediaDescription.displayTitle
                                        it.displayDescription = podcast.description
                                        it.albumArtUri = mediaDescription.albumArtUri.toString()
                                        it.displayIconUri = mediaDescription.displayIconUri.toString()
                                        it.date = podcast.updated.toString()
                                        it.inDatabaseStatus = mediaDescription.inDatabaseStatus
                                    }.build().fullDescription
                            val newMediaItem = MediaItem(newMediaDescription, FLAG_BROWSABLE)
                            return newMediaItem
                        }
            }
        }
        return mediaItem
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
                episode.queuePosition = this.queuePosition
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