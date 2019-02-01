package com.caldeirasoft.basicapp.presentation.datasource

import androidx.lifecycle.*
import androidx.paging.*
import com.caldeirasoft.basicapp.domain.entity.Podcast
import com.caldeirasoft.basicapp.domain.repository.ItunesRepository
import com.caldeirasoft.basicapp.domain.repository.PodcastRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Created by Edmond on 15/02/2018.
 */
open class ItunesBaseDataSource(
        val itunesRepository: ItunesRepository,
        val podcastRepository: PodcastRepository
) : PageKeyedDataSource<Int, Podcast>()
{
    // LiveData of Request status.
    protected val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    // LiveData of network errors.
    protected val _networkErrors = MutableLiveData<String>()
    val networkErrors: LiveData<String>
        get() = _networkErrors

    var startPosition:Int = 0
    var ids:List<Int> = arrayListOf()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Podcast>) {
        GlobalScope.launch {
            try {
                _isLoading.postValue(true)
                // request Ids
                val podcasts = loadPodcasts(0, 1, params.requestedLoadSize).await()
                callback.onResult(podcasts, null, 1)
            }
            catch (e:Throwable) {
                _networkErrors.postValue(e.message)
            }
            finally {
                _isLoading.postValue(false)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int> , callback: LoadCallback<Int, Podcast>)
    {
        GlobalScope.launch {
            try {
                val page = params.key
                _isLoading.postValue(true)
                // request Ids
                val podcasts = loadPodcasts( page, page+1, params.requestedLoadSize).await()
                callback.onResult(podcasts, page + 1)
            }
            catch (e:Throwable) {
                _networkErrors.postValue(e.message)
            }
            finally {
                _isLoading.postValue(false)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int> , callback: LoadCallback<Int, Podcast>) {
    }

    fun loadPodcasts(requestedPage:Int, adjacentPage:Int, requestedLoadSize:Int): Deferred<List<Podcast>> =
        GlobalScope.async {
            val podcastsList: ArrayList<Podcast> = arrayListOf()
            val startPosition = requestedPage * requestedLoadSize
            if (startPosition > ids.size - 1) {
                return@async podcastsList
            }

            val idsSubset = ids.subList(startPosition, Math.min(ids.size - 1, startPosition + requestedLoadSize))
            // request Podcasts from Ids
            if (idsSubset.isNotEmpty()) {
                itunesRepository.lookup(idsSubset).await().let { list ->
                    //TODO: check if podcast is in database
                    podcastsList.addAll(list)

                    /*
            // test podcasts on db
            doAsync {
                val podcastsFromDb = podcastDao.getPodcasts()
                podcast.isInDatabase = podcastsFromDb.value!!.any { cast -> cast.feedId == podcast.feedId }
                //podcast.isInDatabase = true
            }
            */
                }
            }
            podcastsList
        }
}