package com.caldeirasoft.castly.domain.datasource

import com.caldeirasoft.castly.domain.model.NetworkState
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.repository.ItunesRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository

/**
 * Created by Edmond on 15/02/2018.
 */
abstract class ItunesBaseDataSource(
        val itunesRepository: ItunesRepository,
        val podcastRepository: PodcastRepository
) : BasePagingDataSource<Podcast>()
{
    override fun loadInitialFromSource(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Podcast>) {
        val podcasts = loadPodcasts(0, 1, params.requestedLoadSize)
        handleLoadInitialSuccess(params, callback, podcasts)
    }

    override fun loadAfterFromSource(params: LoadParams<Int>, callback: LoadCallback<Int, Podcast>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Podcast>) {
        try {
            networkState.postValue(NetworkState.Loading)
            // request Ids
            val podcasts = loadPodcasts(0, 1, params.requestedLoadSize)
            callback.onResult(podcasts, null, 2)
            networkState.postValue(NetworkState.Success)
        }
        catch (e:Throwable) {
            networkState.postValue(NetworkState.Error(e))
        }
    }

    override fun loadAfter(params: LoadParams<Int> , callback: LoadCallback<Int, Podcast>)
    {
        try {
            val page = params.key
            networkState.postValue(NetworkState.Loading)
            // request Ids
            val podcasts = loadPodcasts( page, page+1, params.requestedLoadSize)
            callback.onResult(podcasts, params.key.inc())
            networkState.postValue(NetworkState.Success)
        }
        catch (e:Throwable) {
            networkState.postValue(NetworkState.Error(e))
        }
    }

    override fun loadBefore(params: LoadParams<Int> , callback: LoadCallback<Int, Podcast>) {
    }


    protected abstract fun loadPodcasts(requestedPage:Int, adjacentPage:Int, requestedLoadSize:Int): List<Podcast>
}