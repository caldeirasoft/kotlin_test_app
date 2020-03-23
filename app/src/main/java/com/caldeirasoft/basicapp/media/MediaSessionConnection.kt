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
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.media2.common.MediaItem
import androidx.media2.common.SessionPlayer.PLAYER_STATE_IDLE
import androidx.media2.session.MediaBrowser
import androidx.media2.session.MediaController
import androidx.media2.session.SessionCommandGroup
import androidx.media2.session.SessionToken
import com.caldeirasoft.basicapp.media.MediaSessionConnection.MediaBrowserConnectionCallback
import com.caldeirasoft.basicapp.presentation.utils.SingleLiveEvent
import java.util.concurrent.CountDownLatch

/**
 * Class that manages a connection to a [MediaLibraryService] instance.
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
class MediaSessionConnection(val context: Context, serviceComponent: ComponentName)
{
    private val sessionToken = SessionToken(context, serviceComponent)
    private var mediaBrowserProxyCallbacksList: MutableList<MediaBrowser.BrowserCallback> = arrayListOf()

    val isConnected = MutableLiveData<Boolean>()
            .apply { postValue(false) }

    val playbackState = MutableLiveData<Int>()
            .apply { postValue(PLAYER_STATE_IDLE) }
    val nowPlaying = MutableLiveData<MediaItem>()
            .apply { postValue(null) }

    val playbackStateChangedEvent = SingleLiveEvent<Int>()
    val metadataChangedEvent = SingleLiveEvent<MediaItem>()

    var queueList: MutableLiveData<List<MediaItem>> = MutableLiveData()

    fun getMediaBrowser(callback: MediaBrowser.BrowserCallback): MediaBrowser {
        val mediaBrowser = MediaBrowser.Builder(context)
                .setControllerCallback(ContextCompat.getMainExecutor(context), callback)
                .setSessionToken(sessionToken)
                .build()
        return mediaBrowser
    }

    inner abstract class MediaBrowserConnectionCallback : MediaBrowser.BrowserCallback() {
        val isConnected = MutableLiveData<Boolean>()
                .apply { postValue(false) }
        var onConnectedAction: (() -> Unit)? = null
        val connectLatch: CountDownLatch = CountDownLatch(1)

        fun setConnectionAction(connectedAction: (() -> Unit)?) {
            this.onConnectedAction = connectedAction
        }

        override fun onConnected(controller: MediaController, allowedCommands: SessionCommandGroup) {
            super.onConnected(controller, allowedCommands)
            queueList.postValue(controller.playlist)
        }
    }

    /*
    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            println("onPlaybackStateChanged")
            playbackState.postValue(state ?: EMPTY_PLAYBACK_STATE)
            playbackStateChangedEvent.postValue(state ?: EMPTY_PLAYBACK_STATE)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            println("onMetadataChanged")
            nowPlaying.postValue(metadata ?: NOTHING_PLAYING)
            metadataChangedEvent.postValue(metadata)
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
            queueList.postValue(queue)
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
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
    */

    companion object {
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



