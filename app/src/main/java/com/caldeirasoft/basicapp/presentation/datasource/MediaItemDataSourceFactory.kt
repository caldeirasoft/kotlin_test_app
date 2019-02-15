package com.caldeirasoft.basicapp.presentation.datasource

import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.MutableLiveData
import androidx.media2.MediaItem
import androidx.media2.MediaMetadata
import androidx.paging.*
import com.caldeirasoft.basicapp.media.MediaSessionConnection

/**
 * Created by Edmond on 15/02/2018.
 */
class MediaItemDataSourceFactory(
        private val parentId: String,
        private val mediaMetadata: MediaMetadata,
        private val mediaSessionConnection: MediaSessionConnection
) : DataSource.Factory<Int, MediaItem>() {

    val sourceLiveData = MutableLiveData<MediaItemDataSource>()

    override fun create(): DataSource<Int, MediaItem> {
        val source = MediaItemDataSource(parentId, mediaMetadata, mediaSessionConnection)
        sourceLiveData.postValue(source)
        return source
    }
}