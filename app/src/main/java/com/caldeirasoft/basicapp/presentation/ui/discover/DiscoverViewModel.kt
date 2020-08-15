package com.caldeirasoft.basicapp.presentation.ui.discover

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagedList
import com.caldeirasoft.basicapp.util.Event
import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.domain.model.itunes.GroupingPageData
import com.caldeirasoft.castly.domain.usecase.FetchPodcastsFromItunesUseCase
import com.caldeirasoft.castly.domain.usecase.GetItunesGroupingDataUseCase
import com.caldeirasoft.castly.domain.util.Resource
import com.caldeirasoft.castly.domain.util.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@kotlinx.coroutines.FlowPreview
@kotlinx.coroutines.ExperimentalCoroutinesApi
class DiscoverViewModel(
        getItunesStoreUseCase: GetItunesGroupingDataUseCase,
        fetchPodcastsFromItunesUseCase: FetchPodcastsFromItunesUseCase)
    : ViewModel() {

    companion object {
        val PAGE_SIZE = 15
        val DEFAULT_CATEGORY = 26
        val STORE_FRONT = "143442-3,26"
    }

    // grouping page data flow
    private val groupingPageDataFlow: Flow<Resource<GroupingPageData>> by lazy {
        getItunesStoreUseCase
                .execute(STORE_FRONT, DEFAULT_CATEGORY)
                .flowOn(Dispatchers.IO)
    }

    // loading status
    val groupingPageLoadingStatus: LiveData<Status> =
            groupingPageDataFlow
                    .map { it.status }
                    .distinctUntilChanged()
                    .asLiveData()

    // error status
    val groupingPageErrorMessage: LiveData<Event<String?>> =
            groupingPageDataFlow
                    .map { Event(it.message) }
                    .distinctUntilChanged()
                    .asLiveData()

    // grouping page data
    val groupingPageData: LiveData<GroupingPageData?> =
            groupingPageDataFlow
                    .map { it.data }
                    .distinctUntilChanged()
                    .asLiveData()

    // header data
    val groupingPageHeaderData: LiveData<GroupingPageData.TrendingCollection?> =
            groupingPageDataFlow
                    .map {
                        it.data
                                ?.items
                                ?.filterIsInstance<GroupingPageData.TrendingCollection>()
                                ?.filter { it.isHeader }
                                ?.firstOrNull()
                    }
                    .asLiveData()

    // top podcasts
    val topPodcastsData: Flow<PagedList<Podcast>> =
            fetchPodcastsFromItunesUseCase.fetchAll(viewModelScope, 15, DEFAULT_CATEGORY).pagedList

    init {
        viewModelScope.launch(Dispatchers.IO) {
/*            getItunesStoreUseCase.execute(STORE_FRONT, DEFAULT_CATEGORY)
                    .onEach { _groupingPageLoadingStatusData.postValue(it.status) }
                    .onEach { _groupingPageData.postValue(it.data) }
                    .onEach { _groupingPageErrorMessageData.postValue(Event(it.message)) }*/
        }
    }
}
