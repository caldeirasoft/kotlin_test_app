package com.caldeirasoft.basicapp.presentation.datasource

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.session.PlaybackStateCompat
import androidx.paging.*
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.service.playback.MediaService.Companion.EXTRA_CONTINUATION
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.EXTRA_PODCAST
import com.caldeirasoft.castly.service.playback.extensions.id
import com.caldeirasoft.castly.service.playback.extensions.playbackStatus

/**
 * Created by Edmond on 15/02/2018.
 */
class MediaItemDataSource(
        private val parentId: String,
        private val mediaItem: MediaItem?,
        private val sectionData: LiveData<SectionState>,
        private val mediaSessionConnection: MediaSessionConnection,
        private val dataProvider: MediaItemDataProvider
) : PositionalDataSource<MediaItem>() {
    // Media items
    private val loadedPages = hashSetOf<Int>()

    // LiveData of Request status.
    val isLoading = MutableLiveData<Boolean>()
    // LiveData of network errors.
    private val _networkErrors = MutableLiveData<String>()
    val networkErrors = _networkErrors as LiveData<String>

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<MediaItem>) {
        val extra = getInitialPageBundle(params)
        isLoading.postValue(true)

        if (dataProvider.retrievedMediaItems.size == 0) {
            // load from service
            mediaSessionConnection.subscribe(parentId, extra, object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(parentId: String, children: MutableList<MediaItem>, options: Bundle) {
                    loadedPages.add(0)
                    dataProvider.addItems(children, options.getString(EXTRA_CONTINUATION))

                    println("params.requestedStartPosition ${params.requestedStartPosition}")
                    updatePlaybackState(children)
                    callback.onResult(children, Math.max(params.requestedStartPosition, 0))
                    isLoading.postValue(false)
                    mediaSessionConnection.unsubscribe(parentId, this)
                }
            })
        } else {
            // load from cache
            loadedPages.add(0)
            val mediaItems =
                    dataProvider.retrievedMediaItems
                            .map { MediaItem(it.description, MediaItem.FLAG_BROWSABLE) }
                            .toList()

            updatePlaybackState(mediaItems)
            callback.onResult(mediaItems, Math.max(params.requestedStartPosition, 0))
            isLoading.postValue(false)
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<MediaItem>) {
        val pageIndex = getPageIndex(params)
        // already loaded : return empty
        if (loadedPages.contains(pageIndex)) {
            callback.onResult(arrayListOf())
            return
        }

        // is section : return empty
        if (dataProvider.isFull) {
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
        mediaSessionConnection.subscribe(parentId, extra, object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(parentId: String, children: MutableList<MediaItem>, options: Bundle) {
                loadedPages.add(0)
                dataProvider.addItems(children, options.getString(EXTRA_CONTINUATION))

                callback.onResult(children)
                isLoading.postValue(false)
                mediaSessionConnection.unsubscribe(parentId, this)
            }
        })
    }

    private fun getInitialPageBundle(params: PositionalDataSource.LoadInitialParams): Bundle {
        return Bundle().apply {
            putInt(MediaBrowserCompat.EXTRA_PAGE, 0)
            putInt(MediaBrowserCompat.EXTRA_PAGE_SIZE, params.pageSize)
            mediaItem?.let {
                this.putParcelable(EXTRA_PODCAST, it)
            }
        }
    }

    private fun getRangeBundle(params: PositionalDataSource.LoadRangeParams): Bundle {
        return Bundle().apply {
            putInt(MediaBrowserCompat.EXTRA_PAGE, getPageIndex(params))
            putInt(MediaBrowserCompat.EXTRA_PAGE_SIZE, params.loadSize)
            putString(EXTRA_CONTINUATION, dataProvider.continuation)
            mediaItem?.let {
                this.putParcelable(EXTRA_PODCAST, it)
            }
        }
    }

    private fun getPageIndex(params: PositionalDataSource.LoadRangeParams): Int {
        return params.startPosition / params.loadSize
    }

    private fun updatePlaybackState(mediaItems: List<MediaBrowserCompat.MediaItem>) {
        mediaItems.forEach { item ->
            val mediaMetadata = mediaSessionConnection.nowPlaying.value
            val playbackState = mediaSessionConnection.playbackState.value
            item.description.playbackStatus =
                    if (item.mediaId == mediaMetadata?.id) {
                        when (playbackState?.state) {
                            PlaybackStateCompat.STATE_PLAYING,
                            PlaybackStateCompat.STATE_BUFFERING ->
                                PlaybackStateCompat.STATE_PLAYING
                            else -> PlaybackStateCompat.STATE_PAUSED
                        }
                    } else PlaybackStateCompat.STATE_NONE
        }
    }

}