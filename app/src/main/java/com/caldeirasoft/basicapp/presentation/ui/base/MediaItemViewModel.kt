package com.caldeirasoft.basicapp.presentation.ui.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.media2.*
import com.caldeirasoft.basicapp.media.MediaBrowserConnectionCallback
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.matchParent
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

abstract class MediaItemViewModel<T>(
        val mediaId: String,
        val mediaSessionConnection: MediaSessionConnection)
    : ViewModel()
{
    private val _mediaItems = MutableLiveData<List<MediaItem>>()
            .apply { postValue(emptyList()) }

    // media items
    val mediaItems: LiveData<List<MediaItem>> = _mediaItems

    // data items
    abstract val dataItems: LiveData<List<T>>

    // subscriptions callback
    private val browserCallback = object : MediaBrowserConnectionCallback() { }

    // media browser
    val mediaBrowser : MediaBrowser

    init {
        // init media browser
        mediaBrowser = mediaSessionConnection.getMediaBrowser(browserCallback).also {
            GlobalScope.launch {
                if (!it.isConnected)
                    browserCallback.connectLatch.await()

                // And subscribe the media ID to watch.
                Log.d("Refresh", mediaId)
                refresh()
            }
        }
    }

    fun refresh() {
        Log.d("refresh", mediaId)
        if (mediaBrowser.isConnected) {
            // load children
            GlobalScope.launch {
                mediaBrowser.getChildren(mediaId, 0, 200, MediaLibraryService.LibraryParams.Builder().build())
                        .get()
                        .mediaItems
                        .let {
                            _mediaItems.postValue(it)
                        }
            }
        }
    }
}