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
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.*
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.MediaSessionCompat.*
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.media.MediaBrowserServiceCompat
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
import com.caldeirasoft.castly.service.playback.const.Constants
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_PODCAST_GET_DESCRIPTION
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_PODCAST_SUBSCRIBE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_PODCAST_UNSUBSCRIBE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_QUEUE_ADD_ITEM
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_PLAYBACK_UPDATE_INFO
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.CURRENT_PROGRESS
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.EXTRA_DATE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.MEDIA_ROOT
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.METADATA_KEY_IN_DATABASE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.PROGRESS_UPDATE_EVENT
import com.caldeirasoft.castly.service.playback.extensions.*
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    private lateinit var mediaSession: MediaSessionCompat
    //private lateinit var mediaController: MediaControllerCompat
    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var audioManager: AudioManager
    private lateinit var audioFocusRequest: AudioFocusRequest
    private lateinit var mediaSessionCallback: MediaSessionCallback

    private var audioFocusRequested = false
    private var lastInitializedTrack: MediaDescriptionCompat? = null
    private var metadataBuilder = MediaMetadataCompat.Builder()
    private var becomingNoisyReceiverRegistered = false
    private val updateIntervalMs = 1000L
    private val progressHandler = Handler()
    private var needUpdateProgress = false

    private val stateBuilder: PlaybackStateCompat.Builder = PlaybackStateCompat.Builder()
            .setActions(
                    PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_STOP
                            or PlaybackStateCompat.ACTION_PAUSE
                            or PlaybackStateCompat.ACTION_PLAY_PAUSE
                            or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                            or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)

    private lateinit var becomingNoisyReceiver: BecomingNoisyReceiver
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var notificationBuilder: NotificationBuilder
    //private lateinit var mediaSessionConnectorMananger: MediaSessionConnectorMananger
    private lateinit var packageValidator: PackageValidator

    private val podcastRepository: PodcastRepository by inject()
    private val episodeRepository: EpisodeRepository by inject()
    private val feedlyRepository: FeedlyRepository by inject()

    // Episodes loaded Pages
    private val loadedPages = hashMapOf<Int, String>()
    // Episodes backend
    private val mediaItemsBackstore: HashMap<String, MutableList<MediaItem>> = hashMapOf()

    private val updateProgressTask = Runnable {
        if (needUpdateProgress) {
            val bundle = Bundle().apply {
                putLong(CURRENT_PROGRESS, exoPlayer.currentPosition)
            }
            mediaSession.sendSessionEvent(PROGRESS_UPDATE_EVENT, bundle)
            startUpdateProgress(true)
        }
    }

    // player listener
    private val playerListener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            println("onPlayerStateChanged")
            when (playbackState) {
                STATE_READY -> {
                    val duration = exoPlayer.duration
                    if (duration >= 0) {
                        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                        mediaSession.setMetadata(metadataBuilder.build())
                        mediaSessionCallback.onUpdateCurrentTrackDuration(duration)
                    }
                }
                STATE_ENDED -> {
                    if (playWhenReady) {
                        mediaSessionCallback.onSkipToNext()
                    }
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
            when (reason) {
                DISCONTINUITY_REASON_PERIOD_TRANSITION -> mediaSessionCallback.onPlayNext()
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
    }

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> mediaSessionCallback.onPlay()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> mediaSessionCallback.onPause()
            else -> mediaSessionCallback.onPause()
        }
    }

    // player media source
    private val mediaSource = ConcatenatingMediaSource()

    companion object {
        const val ID = "TEST"

        const val EXTRA_MEDIA_ID = "android.media.browse.extra.media_id"
        const val EXTRA_PODCAST = "android.media.browse.extra.podcast"
        const val EXTRA_CONTINUATION = "android.media.browse.extra.continuation"
        const val EXTRA_SECTION = "android.media.browse.extra.section"
        const val EXTRA_RELOAD_ALL = "android.media.browse.extra.reload"

        const val NOTIFICATION_ID = 888

        const val MEDIA_ID_GET_ITEM = "media_id_get_item"
    }


    private var isForegroundService = false


    override fun onCreate() {
        super.onCreate()

        // audio focus
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()

            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setOnAudioFocusChangeListener(audioFocusChangeListener)
                    .setAcceptsDelayedFocusGain(false)
                    .setWillPauseWhenDucked(true)
                    .setAudioAttributes(audioAttributes)
                    .build()
        }

        // Build a PendingIntent that can be used to launch the UI.
        val sessionIntent = packageManager?.getLaunchIntentForPackage(packageName)
        val sessionActivityPendingIntent = PendingIntent.getActivity(this, 0, sessionIntent, 0)
        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON, null, applicationContext, MediaButtonReceiver::class.java)

        // Initialize ExoPlayer
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, DefaultRenderersFactory(this), DefaultTrackSelector(), DefaultLoadControl())
        exoPlayer.addListener(playerListener)

        val userAgent =  Util.getUserAgent(this, this.getString(R.string.app_name))

        // Default parameters, except allowCrossProtocolRedirects is true
        val httpDataSourceFactory = DefaultHttpDataSourceFactory(
                userAgent, null,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true)

        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory = DefaultDataSourceFactory(this, DefaultBandwidthMeter(), httpDataSourceFactory)

        // Media session callback
        mediaSessionCallback = MediaSessionCallback(dataSourceFactory)

        // Create a new MediaSession.
        mediaSession = MediaSessionCompat(this, "MediaService").apply{
            setFlags(FLAG_HANDLES_MEDIA_BUTTONS or FLAG_HANDLES_TRANSPORT_CONTROLS or FLAG_HANDLES_QUEUE_COMMANDS)
            setCallback(mediaSessionCallback)
            setMediaButtonReceiver(PendingIntent.getBroadcast(applicationContext, 0, mediaButtonIntent, 0))
            controller.registerCallback(ControllerCallback())
        }

        /**
         * In order for [MediaBrowserCompat.ConnectionCallback.onConnected] to be called,
         * a [MediaSessionCompat.Token] needs to be set on the [MediaBrowserServiceCompat].
         *
         * It is possible to wait to set the session token, if required for a specific use-case.
         * However, the token *must* be set by the time [MediaBrowserServiceCompat.onGetRoot]
         * returns, or the connection will fail silently. (The system will not even call
         * [MediaBrowserCompat.ConnectionCallback.onConnectionFailed].)
         */
        sessionToken = mediaSession.sessionToken

        // Sets the notification manager
        notificationBuilder = NotificationBuilder(this)
        notificationManager = NotificationManagerCompat.from(this)
        becomingNoisyReceiver = BecomingNoisyReceiver(context = this, sessionToken = mediaSession.sessionToken)
    }

    override fun onDestroy() {
        exoPlayer.release()
        mediaSession.run {
            isActive = false
            release()
        }
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
                    mediaItems.addAll(this@MediaService.onLoadEpisodes(parentId, episodeRepository.fetchSync(mediaType.value)))
                    result.sendResult(mediaItems)
                }
            }
            SectionState.PODCAST -> {
                val podcastUrl = mediaId
                val page = options.getInt(MediaBrowserCompat.EXTRA_PAGE)
                val pageSize = options.getInt(MediaBrowserCompat.EXTRA_PAGE_SIZE)
                val reloadAll = options.getBoolean(EXTRA_RELOAD_ALL)

                result.detach()
                GlobalScope.launch {
                    if (reloadAll != true) {
                        if (page == 0)
                            mediaItemsBackstore.clear()

                        val section = options.getInt(EXTRA_SECTION)
                        when (section) {
                            SectionState.QUEUE.value,
                            SectionState.INBOX.value,
                            SectionState.FAVORITE.value,
                            SectionState.HISTORY.value -> {
                                mediaItems.addAll(onLoadEpisodes(parentId, episodeRepository.fetchSync(section, podcastUrl), true))
                            }
                            else -> {
                                val continuation = loadedPages.get(page - 1)
                                val episodes: List<Episode> = onLoadEpisodesFromFeedly(podcastUrl, options, page, pageSize, continuation)
                                mediaItems.addAll(onLoadEpisodes(parentId, episodes, true))
                            }
                        }
                    } else {
                        val episodesFromDb = episodeRepository.fetchSync(podcastUrl)
                        mediaItems.addAll(mediaItemsBackstore[parentId].orEmpty())
                    }

                    result.sendResult(mediaItems)
                }
            }
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
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

            else -> {
                result.detach()
            }
        }
    }

    private fun initTrack(track: MediaDescriptionCompat?) {
        track?.let {
            metadataBuilder.apply {
                id = it.metadata.id.orEmpty()
                title = it.metadata.title
                artist = it.metadata.artist
                album = it.metadata.album
                albumArtUri = it.metadata.albumArtUri.toString()
                mediaUri = it.metadata.mediaUri.toString()
                date = it.metadata.date
                duration = -1
            }

            mediaSession.setMetadata(metadataBuilder.build())
            lastInitializedTrack = it
        }

        sendPlaylistInfoEvent()
    }

    private fun play(track: MediaDescriptionCompat?) {
        if (track == null)
            return

        var trackChanged = false
        if (lastInitializedTrack?.mediaUri != track.mediaUri) {
            initTrack(track)
            trackChanged = true
        }

        if (!requestAudioFocus())
            return

        val currentPosition = if (trackChanged) 0L else exoPlayer.currentPosition
        mediaSession?.apply {
            isActive = true
            setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, currentPosition, 1F).build())
            //notificationManager
            sendSessionEvent("eee", null)
        }

        exoPlayer.playWhenReady = true
        startUpdateProgress()
    }

    private fun requestAudioFocus(): Boolean {
        if (!audioFocusRequested) {
            audioFocusRequested = true

            val audioFocusResult: Int =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) audioManager.requestAudioFocus(audioFocusRequest)
                    else audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)

            if (audioFocusResult != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun abandonAudioFocus() {
        if (!audioFocusRequested) {
            return
        }
        audioFocusRequested = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest)
        } else {
            audioManager.abandonAudioFocus(audioFocusChangeListener)
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
    private fun onLoadEpisodes(parentId: String, episodes: List<Episode>, addToBackstore: Boolean = false): MutableList<MediaItem> {
        val mediaItems =
                episodes.map { MediaItem(it.mediaDescription, FLAG_PLAYABLE) }.toMutableList()
        if (addToBackstore) {
            this.mediaItemsBackstore.apply {
                if (this.contains(parentId))
                    this.get(parentId)?.addAll(mediaItems)
                else
                    this.put(parentId, mediaItems.toMutableList())
            }
        }
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
            continuation: String?,
            alreadyRetrievedEpisodes: MutableList<Episode> = java.util.ArrayList()): List<Episode> {

        val episodes: MutableList<Episode> = arrayListOf()
        val podcast: Podcast? =
                params.getParcelable<MediaItem>(EXTRA_PODCAST)?.toPodcast()
                        ?: podcastRepository.getSync(podcastUrl)
        podcast?.let {
            episodes.addAll(onLoadEpisodesFromFeedly(it, page, pageSize, continuation, alreadyRetrievedEpisodes))
        }
        return episodes
    }

    /**
     * Get episodes from feedly
     */
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

    /**
     * Subscribe to a new podcast
     */
    private fun subscribeToPodcast(mediaItem: MediaItem): MediaItem {
        // get podcast info
        val podcastUrl = MediaID().fromString(mediaItem.mediaId.orEmpty()).id
        podcastUrl.let { url ->
            feedlyRepository
                    .getPodcastFromFeedlyApi(url)
                    ?.let {
                        // mediaItem url
                        it.title = mediaItem.description.metadata?.title.orEmpty()
                        it.authors = mediaItem.description.metadata?.artist
                        it.imageUrl = mediaItem.description.metadata?.albumArtUri.toString()

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
        mediaItem.description.extras?.remove(METADATA_KEY_IN_DATABASE)
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
    private fun getPodcastDescription(mediaItem: MediaItem): MediaItem {
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
                                        mediaDescription.metadata?.apply {
                                            it.title = title
                                            it.artist = artist
                                            it.displayTitle = displayTitle
                                            it.displayDescription = podcast.description
                                            it.albumArtUri = albumArtUri.toString()
                                            it.displayIconUri = displayIconUri.toString()
                                            it.date = podcast.updated.toString()
                                            it.inDatabaseStatus = inDatabaseStatus
                                        }
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
            mediaSession.controller.playbackState?.let { updateNotification(it) }
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            state?.let { updateNotification(it) }
        }

        private fun updateNotification(state: PlaybackStateCompat) {
            val updatedState = state.state
            if (mediaSession.controller.metadata == null) {
                return
            }

            // Skip building a notification when state is "none".
            val notification = if (updatedState != PlaybackStateCompat.STATE_NONE) {
                notificationBuilder.buildNotification(mediaSession.sessionToken)
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

    /**
     * Inner class to receive callbacks about session changes to the [MediaSessionCompat]. In response
     * to those callbacks, this class:
     */
    private inner class MediaSessionCallback(val dataSourceFactory: DataSource.Factory) : MediaSessionCompat.Callback() {

        private var curIndex = -1
        private val playList = ArrayList<MediaDescriptionCompat>()
        private val currentTrack: MediaDescriptionCompat?
            get() = playList.getOrNull(curIndex)

        override fun onPlay() {
            println("onPlay")
            play(currentTrack)
        }

        override fun onPause() {
            println("onPause")
            exoPlayer.playWhenReady = false

            stopUpdateProgress()
            abandonAudioFocus()

            mediaSession.setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, exoPlayer.currentPosition, 1F).build())
        }

        override fun onStop() {
            println("onStop")
            exoPlayer.stop()

            stopUpdateProgress()
            abandonAudioFocus()

            mediaSession.apply {
                isActive = false
                setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_STOPPED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1F).build())
            }
        }

        fun onClear() {
            println("onClear")
            exoPlayer.stop()

            stopUpdateProgress()
            abandonAudioFocus()

            playList.clear()
            mediaSource.clear()
            curIndex = -1
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
                    mediaSession.setMetadata(metadataBuilder.build())
                    sendPlaylistInfoEvent()
                }
            }
        }

        override fun onCustomAction(action: String?, extras: Bundle?) {
            println("onCustomAction")
        }

        override fun onSkipToNext() {
            println("onSkipToNext")
            if (curIndex == mediaSource.size - 1) {
                 onClear()
            }
            else {
                curIndex++
                onPlay()
            }
        }

        fun onPlayNext() {
            curIndex++
            onPlay()
        }

        override fun onSkipToPrevious() {
            println("onSkipToPrevious")
        }

        override fun onAddQueueItem(description: MediaDescriptionCompat) {
            println("onAddQueueItem")

            playList.add(description)
            mediaSource.addMediaSource(buildMediaSource(description, dataSourceFactory))
            if (mediaSource.size == 1) {
                curIndex = 0
                exoPlayer.prepare(mediaSource)
                play(currentTrack)
            }
        }

        override fun onAddQueueItem(description: MediaDescriptionCompat, index: Int) {
            println("onAddQueueItem")

            playList.add(index, description)
            mediaSource.addMediaSource(index, buildMediaSource(description, dataSourceFactory))
        }

        override fun onRemoveQueueItem(description: MediaDescriptionCompat) {
            println("onRemoveQueueItem")

            val index = playList.indexOfFirst { it.mediaId == description.mediaId }
            playList.removeAt(index)
            mediaSource.removeMediaSource(index)
        }

        fun onUpdateCurrentTrackDuration(duration: Long) {
            currentTrack?.let {
                GlobalScope.launch {
                    episodeRepository.getSync(it.mediaId.orEmpty())?.let { episode ->
                        if (episode.duration != duration)
                        {
                            episode.duration = duration
                            episodeRepository.update(episode)
                        }
                    }

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
    }
}