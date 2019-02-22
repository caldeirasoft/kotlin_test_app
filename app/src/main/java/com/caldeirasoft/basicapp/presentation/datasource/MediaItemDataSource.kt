package com.caldeirasoft.basicapp.presentation.datasource

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media2.*
import androidx.media2.MediaController.ControllerResult.RESULT_CODE_SUCCESS
import androidx.paging.*
import androidx.versionedparcelable.ParcelUtils
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.service.playback.PodcastLibraryService.Companion.EXTRA_CONTINUATION
import com.caldeirasoft.castly.service.playback.PodcastLibraryService.Companion.EXTRA_PODCAST
import com.caldeirasoft.castly.service.playback.PodcastLibraryService.Companion.EXTRA_RELOAD_ALL
import java.util.concurrent.CountDownLatch

/**
 * Created by Edmond on 15/02/2018.
 */
class MediaItemDataSource(
        private val parentId: String,
        private val mediaItem: MediaItem?,
        private val sectionData: LiveData<SectionState>,
        private val mediaBrowser: MediaBrowser,
        private val connectLatch: CountDownLatch,
        private val dataProvider: MediaItemDataProvider
) : PositionalDataSource<MediaItem>()
{
    // Media items
    private val loadedPages = hashSetOf<Int>()

    // continuation
    private var continuation: String? = null
    private var firstConnect: Boolean = true

    // LiveData of Request status.
    val isLoading = MutableLiveData<Boolean>()
    // LiveData of network errors.
    private val _networkErrors = MutableLiveData<String>()
    val networkErrors= _networkErrors as LiveData<String>

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<MediaItem>) {
        val extra = getInitialPageBundle(params)
        isLoading.postValue(true)

        if (!mediaBrowser.isConnected)
            connectLatch.await()

        val libraryParams = MediaLibraryService.LibraryParams.Builder().setExtras(extra).build()
        val result = mediaBrowser.getChildren(parentId, 0, params.pageSize, libraryParams).get()
        if (result.resultCode == RESULT_CODE_SUCCESS)
        {
            loadedPages.add(0)
            result.mediaItems.orEmpty().apply {
                dataProvider.retrievedMediaItemsCount = this.size
                callback.onResult(this, params.requestedStartPosition)
            }
            continuation = result.libraryParams?.extras?.getString(EXTRA_CONTINUATION)
        }
        else {
            callback.onResult(mutableListOf<MediaItem>(), params.requestedStartPosition)
        }
        isLoading.postValue(false)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<MediaItem>) {
        val pageIndex = getPageIndex(params)
        // already loaded : return empty
        if (loadedPages.contains(pageIndex)) {
            callback.onResult(arrayListOf())
            return
        }

        // is section : return empty
        if (sectionData.value != null) {
            callback.onResult(arrayListOf())
            return
        }

        val extra = getRangeBundle(params)
        isLoading.postValue(true)
        val libraryParams = MediaLibraryService.LibraryParams.Builder().setExtras(extra).build()
        val result = mediaBrowser.getChildren(parentId, pageIndex, params.loadSize, libraryParams).get()
        if (result.resultCode == RESULT_CODE_SUCCESS)
        {
            loadedPages.add(pageIndex)
            result.mediaItems.orEmpty().apply {
                dataProvider.retrievedMediaItemsCount = (dataProvider.retrievedMediaItemsCount ?: 0) + this.size
                callback.onResult(this)
            }
            continuation = result.libraryParams?.extras?.getString(EXTRA_CONTINUATION)
        }
        else {
            callback.onResult(mutableListOf<MediaItem>())
        }
        isLoading.postValue(false)
    }

    private fun getInitialPageBundle(params: PositionalDataSource.LoadInitialParams): Bundle {
        return Bundle().apply {
            mediaItem?.let {
                ParcelUtils.putVersionedParcelable(this, EXTRA_PODCAST, it)
            }
            putInt(MediaBrowserCompat.EXTRA_PAGE_SIZE, params.pageSize)
            dataProvider.retrievedMediaItemsCount?.let {
                putBoolean(EXTRA_RELOAD_ALL, true)
            }
        }
    }

    private fun getRangeBundle(params: PositionalDataSource.LoadRangeParams): Bundle {
        return Bundle().apply {
            mediaItem?.let {
                ParcelUtils.putVersionedParcelable(this, EXTRA_PODCAST, it)
            }
            putString(EXTRA_CONTINUATION, continuation)
            putInt(MediaBrowserCompat.EXTRA_PAGE_SIZE, params.loadSize)
        }
    }

    private fun getPageIndex(params: PositionalDataSource.LoadRangeParams): Int {
        return params.startPosition / params.loadSize
    }
}