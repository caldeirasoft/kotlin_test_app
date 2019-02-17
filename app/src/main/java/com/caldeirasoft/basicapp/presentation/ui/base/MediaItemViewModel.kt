package com.caldeirasoft.basicapp.presentation.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media2.MediaBrowser
import androidx.media2.MediaItem
import androidx.media2.MediaLibraryService
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
        it.subscribe(mediaId, null)
    }


    override fun onCleared() {
        super.onCleared()
        // And then, finally, unsubscribe the media ID that was being watched.
        mediaBrowser.unsubscribe(mediaId)
    }
}