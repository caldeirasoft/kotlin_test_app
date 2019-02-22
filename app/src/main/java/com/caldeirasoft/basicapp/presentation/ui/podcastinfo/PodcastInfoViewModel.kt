package com.caldeirasoft.basicapp.presentation.ui.podcastinfo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import androidx.media2.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.versionedparcelable.ParcelUtils
import com.caldeirasoft.basicapp.media.MediaBrowserConnectionCallback
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.basicapp.presentation.datasource.MediaItemDataProvider
import com.caldeirasoft.basicapp.presentation.datasource.MediaItemDataSourceFactory
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.MediaID
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.service.playback.PodcastLibraryService.Companion.EXTRA_MEDIA_ID
import com.caldeirasoft.castly.service.playback.PodcastLibraryService.Companion.EXTRA_PODCAST
import com.caldeirasoft.castly.service.playback.const.Constants
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_PODCAST_GET_DESCRIPTION
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_PODCAST_SUBSCRIBE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.COMMAND_CODE_PODCAST_UNSUBSCRIBE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.METADATA_KEY_IN_DATABASE
import com.caldeirasoft.castly.service.playback.extensions.toMediaItem
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class PodcastInfoViewModel(
        val mediaId: String,
        val podcast: Podcast?,
        val podcastRepository: PodcastRepository,
        val episodeRepository: EpisodeRepository,
        val mediaSessionConnection: MediaSessionConnection)
    : ViewModel() {

    // subscriptions callback
    private val browserCallback = object : MediaBrowserConnectionCallback() {
        @SuppressLint("RestrictedApi")
        override fun onConnected(controller: MediaController, allowedCommands: SessionCommandGroup) {
            // add new commands to allow
            allowedCommands.apply {
                addCommand(SessionCommand(Constants.COMMAND_CODE_PODCAST_GET_DESCRIPTION, Bundle()))
                addCommand(SessionCommand(Constants.COMMAND_CODE_PODCAST_SUBSCRIBE, Bundle()))
                addCommand(SessionCommand(Constants.COMMAND_CODE_PODCAST_UNSUBSCRIBE, Bundle()))
            }

            super.onConnected(controller, allowedCommands)
        }
    }

    // media browser
    val mediaBrowser: MediaBrowser

    // podcast media item
    @SuppressLint("RestrictedApi")
    val mediaItemData = MutableLiveData<MediaItem>().apply {
        value = podcast?.toMediaItem()
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
        // init media browser
        mediaBrowser = mediaSessionConnection.getMediaBrowser(browserCallback).also {
            GlobalScope.launch {
                if (!it.isConnected)
                    browserCallback.connectLatch.await()

                // get podcast info
                getPodcastInfo()
            }
        }

        // init source factory
        sourceFactory =
                MediaItemDataSourceFactory(
                        this.mediaId, this.mediaItemData.value, sectionData,
                        mediaBrowser, browserCallback.connectLatch, dataProvider)

        // is loading livedata
        isLoading = Transformations.switchMap(sourceFactory.sourceLiveData) { it.isLoading }
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
                    if (mediaItem.metadata?.extras?.getBoolean(METADATA_KEY_IN_DATABASE) != true)
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
                mediaBrowser.sendCustomCommand(
                        SessionCommand(customAction, Bundle()),
                        Bundle().apply {
                            // add media ID
                            this.putString(EXTRA_MEDIA_ID, mediaId)
                            // add media item if it exists
                            mediaItemData.value?.let { mediaItem ->
                                ParcelUtils.putVersionedParcelable(this, EXTRA_PODCAST, mediaItem)
                            }
                        })
                        .get()
                        .customCommandResult
                        ?.let {
                            ParcelUtils.getVersionedParcelable<MediaItem>(it, EXTRA_PODCAST)
                                    ?.let {
                                        mediaItemData.postValue(it)
                                        futureAction?.invoke()
                                    }
                        }
            }
            catch (e: Exception) {
                Log.d("sendCustomCommand", mediaId, e)
            }
        }
    }
}