package com.caldeirasoft.basicapp.presentation.ui.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
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

    init {
        // init media browser
        mediaSessionConnection.subscribe(mediaId,  object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(parentId: String, children: MutableList<MediaItem>) {
                _mediaItems.postValue(children)
            }
        })
    }

    fun refresh() {
        Log.d("refresh", mediaId)
        if (mediaSessionConnection.isConnected.value == true) {
            //mediaSessionConnection.
        }
    }

    override fun onCleared() {
        mediaSessionConnection.unsubscribe(mediaId, object : MediaBrowserCompat.SubscriptionCallback(){})
    }
}