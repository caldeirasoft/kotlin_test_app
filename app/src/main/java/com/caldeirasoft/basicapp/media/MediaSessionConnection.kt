/*
 * Copyright 2018 Google Inc. All rights reserved.
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

package com.caldeirasoft.basicapp.media

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.caldeirasoft.basicapp.media.MediaSessionConnection.MediaBrowserConnectionCallback

/**
 * Class that manages a connection to a [MediaBrowserServiceCompat] instance.
 *
 * Typically it's best to construct/inject dependencies either using DI or, as UAMP does,
 * using [InjectorUtils]. There are a few difficulties for that here:
 * - [MediaBrowserCompat] is a final class, so mocking it directly is difficult.
 * - A [MediaBrowserConnectionCallback] is a parameter into the construction of
 *   a [MediaBrowserCompat], and provides callbacks to this class.
 * - [MediaBrowserCompat.ConnectionCallback.onConnected] is the best place to construct
 *   a [MediaControllerCompat] that will be used to control the [MediaSessionCompat].
 *
 *  Because of these reasons, rather than constructing additional classes, this is treated as
 *  a black box (which is why there's very little logic here).
 *
 *  This is also why the parameters to construct a [MediaSessionConnection] are simple
 *  parameters, rather than private properties. They're only required to build the
 *  [MediaBrowserConnectionCallback] and [MediaBrowserCompat] objects.
 */
class MediaSessionConnection(val context: Context, val serviceComponent: ComponentName)
{
    val isConnected = MutableLiveData<Boolean>()
            .apply { postValue(false) }

    val rootMediaId: String get() = mediaBrowser.root

    val playbackState = MutableLiveData<PlaybackStateCompat>()
            .apply { postValue(EMPTY_PLAYBACK_STATE) }
    val nowPlaying = MutableLiveData<MediaMetadataCompat>()
            .apply { postValue(NOTHING_PLAYING) }

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    val queue: MutableList<MediaSessionCompat.QueueItem>
        get() = mediaController.queue

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)
    private val mediaBrowser = MediaBrowserCompat(context,
            serviceComponent,
            mediaBrowserConnectionCallback, null)
            .apply { connect() }
    private lateinit var mediaController: MediaControllerCompat

    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        Log.d("Subscribe", parentId)
        mediaBrowser.subscribe(parentId, callback)
    }

    fun subscribe(parentId: String, extra: Bundle, callback: MediaBrowserCompat.SubscriptionCallback) {
        Log.d("Subscribe", parentId)
        mediaBrowser.subscribe(parentId, extra, callback)
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        Log.d("Unsubscribe", parentId)
        mediaBrowser.unsubscribe(parentId, callback)
    }

    fun getItem(mediaId: String, callback: MediaBrowserCompat.ItemCallback) {
        Log.d("GetItem", mediaId)
        mediaBrowser.getItem(mediaId, callback)
    }

    fun sendCustomAction(action:String, extras: Bundle, callback: MediaBrowserCompat.CustomActionCallback) {
        Log.d("sendCustomAction", action)
        mediaBrowser.sendCustomAction(action, extras, callback)
    }

    fun addQueueItem(description: MediaDescriptionCompat) {
        println("addQueueItem")
        mediaController.addQueueItem(description)
    }

    fun addQueueItem(description: MediaDescriptionCompat, index:Int) {
        println("addQueueItem")
        mediaController.addQueueItem(description, index)
    }

    fun removeQueueItem(description: MediaDescriptionCompat) {
        println("removeQueueItem")
        mediaController.removeQueueItem(description)
    }

    private inner class MediaBrowserConnectionCallback(private val context: Context)
        : MediaBrowserCompat.ConnectionCallback() {

        /**
         * Invoked after [MediaBrowserCompat.connect] when the request has successfully
         * completed.
         */
        override fun onConnected() {
            // Get a MediaController for the MediaSession.
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
                //mediaControlCallback.onPlaybackStateChanged(playbackState)
                //mediaControlCallback.onMetadataChanged(metadata)
            }

            isConnected.postValue(true)
        }

        /**
         * Invoked when the client is disconnected from the media browser.
         */
        override fun onConnectionSuspended() {
            isConnected.postValue(false)
        }

        /**
         * Invoked when the connection to the media browser failed.
         */
        override fun onConnectionFailed() {
            isConnected.postValue(false)
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            println("onPlaybackStateChanged")
            playbackState.postValue(state ?: EMPTY_PLAYBACK_STATE)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            println("onMetadataChanged")
            nowPlaying.postValue(metadata ?: NOTHING_PLAYING)
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
        }

        /**
         * Normally if a [MediaBrowserServiceCompat] drops its connection the callback comes via
         * [MediaControllerCompat.Callback] (here). But since other connection status events
         * are sent to [MediaBrowserCompat.ConnectionCallback], we catch the disconnect here and
         * send it on to the other callback.
         */
        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }

    companion object {
        @Suppress("PropertyName")
        val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
                .build()

        @Suppress("PropertyName")
        val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
                .build()

        // For Singleton instantiation.
        @Volatile
        private var instance: MediaSessionConnection? = null

        fun getInstance(context: Context, serviceComponent: ComponentName) =
                instance ?: synchronized(this) {
                    instance ?: MediaSessionConnection(context, serviceComponent)
                            .also { instance = it }
                }
    }
}



