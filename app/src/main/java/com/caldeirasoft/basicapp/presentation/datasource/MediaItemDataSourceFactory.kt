package com.caldeirasoft.basicapp.presentation.datasource

import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.support.v4.media.MediaBrowserCompat.MediaItem
import androidx.paging.*
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.castly.domain.model.SectionState
import java.util.concurrent.CountDownLatch

/**
 * Created by Edmond on 15/02/2018.
 */
class MediaItemDataSourceFactory(
        private val parentId: String,
        private val mediaItem: MediaItem?,
        private val sectionData: LiveData<SectionState>,
        private val mediaSessionConnection: MediaSessionConnection,
        private val dataProvider: MediaItemDataProvider
) : DataSource.Factory<Int, MediaItem>() {

    val sourceLiveData = MutableLiveData<MediaItemDataSource>()

    override fun create(): DataSource<Int, MediaItem> {
        val source = MediaItemDataSource(parentId, mediaItem, sectionData, mediaSessionConnection, dataProvider)
        sourceLiveData.postValue(source)
        return source
    }
}