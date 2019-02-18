package com.caldeirasoft.basicapp.presentation.ui.base

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media2.*
import com.caldeirasoft.basicapp.media.MediaSessionConnection

open class MediaItemViewModel(
        val mediaId: String,
        mediaSessionConnection: MediaSessionConnection)
    : ViewModel()
{
    private val _mediaItems = MutableLiveData<List<MediaItem>>()
            .apply { postValue(emptyList()) }

    val mediaItems: LiveData<List<MediaItem>> = _mediaItems

    // subscriptions callback
    private val browserCallback = object : MediaBrowser.BrowserCallback() {
        override fun onConnected(controller: MediaController, allowedCommands: SessionCommandGroup) {
            (controller as MediaBrowser)
                    .subscribe(mediaId,
                            MediaLibraryService.LibraryParams
                                    .Builder().setExtras(Bundle())
                                    .build())
        }
        override fun onChildrenChanged(browser: MediaBrowser, parentId: String, itemCount: Int, params: MediaLibraryService.LibraryParams?) {
            browser.getChildren(parentId, 0, 5, params)
                    .get()
                    .mediaItems
                    .let {
                        _mediaItems.postValue(it)
                    }
        }
    }

    // mediasession
    private val mediaBrowser = mediaSessionConnection.createBrowser(browserCallback).also {
        it.subscribe(mediaId, MediaLibraryService.LibraryParams.Builder().build())
    }


    override fun onCleared() {
        super.onCleared()
        // And then, finally, unsubscribe the media ID that was being watched.
        mediaBrowser.unsubscribe(mediaId)
    }
}