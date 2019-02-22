package com.caldeirasoft.basicapp.presentation.datasource

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.media2.*
import androidx.media2.MediaController.ControllerResult.RESULT_CODE_SUCCESS
import androidx.paging.*
import androidx.versionedparcelable.ParcelUtils
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.service.playback.PodcastLibraryService.Companion.EXTRA_CONTINUATION
import com.caldeirasoft.castly.service.playback.PodcastLibraryService.Companion.EXTRA_PODCAST
import com.caldeirasoft.castly.service.playback.PodcastLibraryService.Companion.EXTRA_RELOAD_ALL
import com.google.common.util.concurrent.ListenableFuture

/**
 * Created by Edmond on 15/02/2018.
 */
data class MediaItemDataProvider(
        var retrievedMediaItemsCount: Int? = null,
        val continuation: String = "")
{
    // Media items
}