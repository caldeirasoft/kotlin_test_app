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

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import android.content.ComponentName
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.HandlerThread
import android.util.Log
import androidx.media2.*
import androidx.media2.SessionPlayer.*
import com.caldeirasoft.castly.service.playback.const.Constants
import com.caldeirasoft.castly.service.playback.utils.SyncHandler
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

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
    private val ioExecutor: Executor by lazy { getExecutor() }
    private val sessionToken = SessionToken(context, serviceComponent)
    private var mediaBrowserProxyCallbacksList: MutableList<MediaBrowser.BrowserCallback> = arrayListOf()

    val isConnected = MutableLiveData<Boolean>()
            .apply { postValue(false) }

    val playbackState = MutableLiveData<Int>()
            .apply { postValue(PLAYER_STATE_IDLE) }
    val nowPlaying = MutableLiveData<MediaItem>()
            .apply { postValue(null) }

    fun getMediaBrowser(callback: MediaBrowserConnectionCallback, connectedAction: (() -> Unit)? = null): MediaBrowser {
        callback.setConnectionAction(connectedAction)
        return MediaBrowser(context, sessionToken, ioExecutor, callback)
    }

    private fun subscribe(parentId: String, params: MediaLibraryService.LibraryParams) {
        Log.d("Subscribe", parentId)
        //mediaBrowser.subscribe(parentId, params)
    }

    private fun unsubscribe(parentId: String) {
        Log.d("Unsubscribe", parentId)
        //mediaBrowser.unsubscribe(parentId)
    }

    private fun getExecutor(): Executor {
        val handlerThread = HandlerThread("MediaSessionConnection")
        handlerThread.start()
        val sHandler = SyncHandler(handlerThread.looper)

        val executor =  object : Executor {
            override fun execute(command: Runnable?) {
                val handler: SyncHandler?
                synchronized(MediaSessionConnection.javaClass) {
                    handler = sHandler
                }
                handler?.post(command)
            }
        }
        return executor
    }

    private inner class MediaControllerCallback : MediaController.ControllerCallback() {

        //                @SessionPlayer.PlayerState int state) { }
        override fun onPlayerStateChanged(controller: MediaController, state: Int) {
            //PLAYER_STATE_IDLE
            //PLAYER_STATE_PLAYING
            //PLAYER_STATE_PAUSED
            //PLAYER_STATE_ERROR
            playbackState.postValue( state)
        }

        override fun onCurrentMediaItemChanged(controller: MediaController, item: MediaItem?) {
            nowPlaying.postValue(item)
        }

        override fun onPlaylistChanged(controller: MediaController, list: MutableList<MediaItem>?, metadata: MediaMetadata?) {
        }

        override fun onDisconnected(controller: MediaController) {
        }
    }

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

abstract class MediaBrowserConnectionCallback()
    : MediaBrowser.BrowserCallback() {

    val isConnected = MutableLiveData<Boolean>()
            .apply { postValue(false) }
    var onConnectedAction: (() -> Unit)? = null
    val connectLatch: CountDownLatch = CountDownLatch(1)

    fun setConnectionAction(connectedAction: (() -> Unit)?) {
        this.onConnectedAction = connectedAction
    }

    @SuppressLint("RestrictedApi")
    override fun onConnected(controller: MediaController, allowedCommands: SessionCommandGroup) {
        // Get a MediaController for the MediaSession.
        isConnected.postValue(true)
        allowedCommands.addCommand(SessionCommand(Constants.COMMAND_CODE_PODCAST_GET_DESCRIPTION, Bundle()))
        allowedCommands.addCommand(SessionCommand(Constants.COMMAND_CODE_PODCAST_SUBSCRIBE, Bundle()))
        allowedCommands.addCommand(SessionCommand(Constants.COMMAND_CODE_PODCAST_UNSUBSCRIBE, Bundle()))

        connectLatch.countDown()
        onConnectedAction?.invoke()
    }

    override fun onDisconnected(controller: MediaController) {
        isConnected.postValue(false)
    }
}



