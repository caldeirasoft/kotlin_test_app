package com.caldeirasoft.basicapp.presentation.ui.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.caldeirasoft.castly.domain.model.NetworkState
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.model.itunes.StoreCollection
import com.caldeirasoft.castly.domain.model.itunes.StoreData
import com.caldeirasoft.castly.domain.model.itunes.StoreMultiCollection
import com.caldeirasoft.castly.domain.usecase.FetchPodcastsFromItunesUseCase
import com.caldeirasoft.castly.domain.usecase.GetItunesStoreUseCase
import com.caldeirasoft.castly.domain.usecase.base.UseCaseResult

class DiscoverViewModel(
        getItunesStoreUseCase: GetItunesStoreUseCase,
        fetchPodcastsFromItunesUseCase: FetchPodcastsFromItunesUseCase)
    : ViewModel() {

    companion object {
        val PAGE_SIZE = 15
        val DEFAULT_CATEGORY = 26
        val STORE_FRONT = "143442-3,26"
    }


    private val itunesStoreUseCaseResult: UseCaseResult<StoreData> by lazy { getItunesStoreUseCase.execute(STORE_FRONT, DEFAULT_CATEGORY) }
    val itunesStoreData: LiveData<StoreData> by lazy { itunesStoreUseCaseResult.data }
    val itunesStoreInitialState: LiveData<NetworkState> by lazy { itunesStoreUseCaseResult.initialState }

    private val fetchPodcastsFromItunesUseCaseResult by lazy { fetchPodcastsFromItunesUseCase.fetchAll(DEFAULT_CATEGORY) }
    val topItems: LiveData<PagedList<Podcast>> by lazy { fetchPodcastsFromItunesUseCaseResult.data }
    val topItemsInitialState: LiveData<NetworkState> by lazy { fetchPodcastsFromItunesUseCaseResult.initialState }
    val topItemsNetworkState: LiveData<NetworkState> by lazy { fetchPodcastsFromItunesUseCaseResult.networkState }

    fun updatePodcast(podcast: Podcast) {
        itunesStoreData.value?.let {
            it.groups.flatMap { collection ->
                        when (collection) {
                            is StoreCollection -> collection.podcasts
                            is StoreMultiCollection -> collection.multiCollection.flatMap { c -> c.podcasts }
                            else -> ArrayList()
                        }
                    }
                    .filter { podcastSection -> podcastSection.id == podcast.id }
                    .forEach { podcastSection ->
                        podcastSection.name = "CHAAAAAANNNGED !!!"
                    }
            val mutableStoreData = itunesStoreData as MutableLiveData<StoreData>
            mutableStoreData.postValue(it)
        }
    }
}
