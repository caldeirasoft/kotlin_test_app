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

class DiscoverViewModel(
        getItunesStoreUseCase: GetItunesStoreUseCase,
        fetchPodcastsFromItunesUseCase: FetchPodcastsFromItunesUseCase)
    : ViewModel() {

    companion object {
        val PAGE_SIZE = 15
        val DEFAULT_CATEGORY = 26
        val STORE_FRONT = "143442-3,26"
    }


    var itunesStoreData: LiveData<StoreData> = MutableLiveData()
    var itunesStoreInitialState: LiveData<NetworkState> = MutableLiveData()

    val topItems: LiveData<PagedList<Podcast>> = MutableLiveData()
    val topItemsInitialState: LiveData<NetworkState> = MutableLiveData()
    val topItemsNetworkState: LiveData<NetworkState> = MutableLiveData()

    init {
        /*getItunesStoreUseCase.execute(STORE_FRONT, DEFAULT_CATEGORY).let {
            //itunesStoreData = it.data
            itunesStoreInitialState = it.initialState
        }*/

        /*
        fetchPodcastsFromItunesUseCase.fetchAll(DEFAULT_CATEGORY).let {
            topItems = it.data
            topItemsInitialState = it.initialState
            topItemsNetworkState = it.networkState
        }
        */
    }

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
