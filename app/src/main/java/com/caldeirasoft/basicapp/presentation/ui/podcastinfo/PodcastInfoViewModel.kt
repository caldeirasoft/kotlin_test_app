package com.caldeirasoft.basicapp.presentation.ui.podcastinfo

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import androidx.lifecycle.*
import android.support.v4.media.MediaBrowserCompat.MediaItem
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.versionedparcelable.ParcelUtils
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.basicapp.presentation.datasource.MediaItemDataProvider
import com.caldeirasoft.basicapp.presentation.datasource.MediaItemDataSourceFactory
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.MediaID
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.service.playback.MediaService.Companion.EXTRA_MEDIA_ID
import com.caldeirasoft.castly.service.playback.MediaService.Companion.EXTRA_PODCAST
import com.caldeirasoft.castly.service.playback.const.Constants
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_PODCAST_GET_DESCRIPTION
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_PODCAST_SUBSCRIBE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_PODCAST_UNSUBSCRIBE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.METADATA_KEY_IN_DATABASE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.STATUS_IN_DATABASE
import com.caldeirasoft.castly.service.playback.extensions.asMediaItem
import com.caldeirasoft.castly.service.playback.extensions.inDatabaseStatus
import com.caldeirasoft.castly.service.playback.extensions.metadata
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class PodcastInfoViewModel(
        val mediaId: String,
        val podcast: Podcast?,
        val podcastRepository: PodcastRepository,
        val episodeRepository: EpisodeRepository,
        val mediaSessionConnection: MediaSessionConnection)
    : ViewModel() {

    // podcast media item
    @SuppressLint("RestrictedApi")
    val mediaItemData = MutableLiveData<MediaItem>().apply {
        value = podcast?.asMediaItem()
    }

    // data items
    val dataItems: LiveData<List<Episode>> = episodeRepository.fetch(MediaID.fromString(mediaId).id)

    // current section
    var sectionData = MutableLiveData<SectionState>()

    // data provider
    val dataProvider = MediaItemDataProvider()

    // source factory
    val sourceFactory: MediaItemDataSourceFactory

    //mediaItems paged list
    val pagedList: LiveData<PagedList<MediaItem>>
        get() {
            val ioExecutor = Executors.newFixedThreadPool(5)
            val pagedListConfig: PagedList.Config = PagedList.Config.Builder()
                    .setPageSize(PAGE_SIZE)
                    .setEnablePlaceholders(false)
                    .setPrefetchDistance(5)
                    .build()
            // pagedList
            return LivePagedListBuilder(sourceFactory, pagedListConfig)
                    .setFetchExecutor(ioExecutor)
                    .build()
        }

    // is loading livedata
    val isLoading: LiveData<Boolean>

    // is subscribing
    var isSubscribing = MutableLiveData<Boolean>()

    //endregion

    companion object {
        val PAGE_SIZE = 15
    }

    init {
        // init source factory
        sourceFactory =
                MediaItemDataSourceFactory(
                        this.mediaId, this.mediaItemData.value, sectionData,
                        mediaSessionConnection, dataProvider)

        // is loading livedata
        isLoading = Transformations.switchMap(sourceFactory.sourceLiveData) { it.isLoading }

        // init media browser
        getPodcastInfo()
    }

    //hacky way to force reload items (e.g. song sort order changed)
    /*
    fun reloadMediaItems() {
        mediaSessionConnection.unsubscribe(id.asString(), subscriptionCallback)
        mediaSessionConnection.subscribe(id.asString(), subscriptionCallback)
    }
    */

    // change episode filter (by section)
    fun setSection(section: SectionState?) {
        sectionData.postValue(section)
    }

    fun refresh() =
            pagedList.value?.dataSource?.invalidate()

    // toggle subscription
    fun toggleSubscription() {
        mediaItemData.value?.let { mediaItem ->
            val command =
                    if (mediaItem.description.metadata?.inDatabaseStatus != STATUS_IN_DATABASE)
                        COMMAND_CODE_PODCAST_SUBSCRIBE
                    else COMMAND_CODE_PODCAST_UNSUBSCRIBE
            isSubscribing.postValue(true)
            sendCustomMediaItemCommand(command) { isSubscribing.postValue(false) }
        }
    }

    // get podcast info
    fun getPodcastInfo() {
        sendCustomMediaItemCommand(COMMAND_CODE_PODCAST_GET_DESCRIPTION)
    }

    private fun sendCustomMediaItemCommand(customAction: String, futureAction: (() -> Unit)? = null) {
        GlobalScope.launch {
            try {
                val extra = Bundle().apply {
                    // add media ID
                    this.putString(EXTRA_MEDIA_ID, mediaId)
                    // add media item if it exists
                    this.putParcelable(EXTRA_PODCAST, mediaItemData.value)
                }
                mediaSessionConnection.sendCustomAction(customAction, extra,
                        object : MediaBrowserCompat.CustomActionCallback() {
                            override fun onResult(action: String?, extras: Bundle?, resultData: Bundle?) {
                                val mediaItem = resultData?.getParcelable<MediaItem>(EXTRA_PODCAST)
                                mediaItemData.postValue(mediaItem)
                                futureAction?.invoke()
                            }
                        })
            }
            catch (e: Exception) {
                Log.d("sendCustomCommand", mediaId, e)
            }
        }
    }
}