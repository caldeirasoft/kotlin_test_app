package com.caldeirasoft.basicapp.presentation.datasource

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media2.MediaBrowser
import androidx.media2.MediaController.ControllerResult.RESULT_CODE_SUCCESS
import androidx.media2.MediaItem
import androidx.media2.MediaLibraryService
import androidx.media2.MediaMetadata
import androidx.paging.*
import androidx.versionedparcelable.ParcelUtils
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.castly.service.playback.PodcastLibraryService.Companion.EXTRA_CONTINUATION
import com.caldeirasoft.castly.service.playback.PodcastLibraryService.Companion.EXTRA_PODCAST
import com.google.common.util.concurrent.ListenableFuture

/**
 * Created by Edmond on 15/02/2018.
 */
class MediaItemDataSource(
        private val parentId: String,
        private val mediaMetadata: MediaMetadata,
        private val mediaSessionConnection: MediaSessionConnection
) : PositionalDataSource<MediaItem>()
{
    // continuation
    private var continuation: String? = null
    private val browser: MediaBrowser

    // LiveData of Request status.
    val isLoading = MutableLiveData<Boolean>()
    // LiveData of network errors.
    private val _networkErrors = MutableLiveData<String>()
    val networkErrors= _networkErrors as LiveData<String>

    init {
        browser = mediaSessionConnection.createBrowser().also {
            it.subscribe(parentId, null)
        }
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<MediaItem>) {
        val extra = getInitialPageBundle(params)
        isLoading.postValue(true)

        val libraryParams = MediaLibraryService.LibraryParams.Builder().setExtras(extra).build()
        val result = browser.getChildren(parentId, 0, params.pageSize, libraryParams).get()
        if (result.resultCode == RESULT_CODE_SUCCESS)
        {
            callback.onResult(result.mediaItems.orEmpty(), params.requestedStartPosition)
            continuation = result.libraryParams?.extras?.getString(EXTRA_CONTINUATION)
        }
        else {
            callback.onResult(mutableListOf<MediaItem>(), params.requestedStartPosition)
        }
        isLoading.postValue(false)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<MediaItem>) {
        val extra = getRangeBundle(params)
        isLoading.postValue(true)

        val libraryParams = MediaLibraryService.LibraryParams.Builder().setExtras(extra).build()
        val result = browser.getChildren(parentId, 0, params.loadSize, libraryParams).get()
        if (result.resultCode == RESULT_CODE_SUCCESS)
        {
            callback.onResult(result.mediaItems.orEmpty())
            continuation = result.libraryParams?.extras?.getString(EXTRA_CONTINUATION)
        }
        else {
            callback.onResult(mutableListOf<MediaItem>())
        }
        isLoading.postValue(false)
    }

    private fun getInitialPageBundle(params: PositionalDataSource.LoadInitialParams): Bundle {
        return Bundle().apply {
            ParcelUtils.putVersionedParcelable(this, EXTRA_PODCAST, mediaMetadata)
            putInt(MediaBrowserCompat.EXTRA_PAGE_SIZE, params.pageSize)
        }
    }

    private fun getRangeBundle(params: PositionalDataSource.LoadRangeParams): Bundle {
        return Bundle().apply {
            ParcelUtils.putVersionedParcelable(this, EXTRA_PODCAST, mediaMetadata)
            putString(EXTRA_CONTINUATION, continuation)
            putInt(MediaBrowserCompat.EXTRA_PAGE_SIZE, params.loadSize)
        }
    }
}