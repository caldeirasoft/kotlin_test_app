package com.caldeirasoft.basicapp.data.repository

import androidx.lifecycle.*
import androidx.paging.*
import com.avast.android.githubbrowser.extensions.retrofitCallback
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.api.itunes.retrofit.ITunesAPI
import com.caldeirasoft.basicapp.data.db.podcasts.PodcastDao
import com.caldeirasoft.basicapp.util.LoadingState
import org.jetbrains.anko.doAsync
import java.util.concurrent.Executor

/**
 * Created by Edmond on 15/02/2018.
 */
class PodcastItunesDataSource(
        val itunesApi: ITunesAPI,
        val podcastDao: PodcastDao,
        val genre: Int
) : PositionalDataSource<Podcast>()
{
    var startPosition:Int = 0
    var ids:List<String> = arrayListOf<String>()
    val loadingState = MutableLiveData<LoadingState>()

    fun loadInternal(startPosition:Int, pageSize:Int, callback: (List<Podcast>) -> Unit)
    {
        val idsSubset = ids.subList(startPosition, startPosition + pageSize)

        // request Podcasts from Ids
        if (idsSubset.isNotEmpty()) {
            val idsJoin = idsSubset.joinToString(",")
            var requestItems = itunesApi.lookup(idsJoin);
            requestItems.enqueue(retrofitCallback(
                    { response ->
                        if (response.isSuccessful) {
                            val podcasts = arrayListOf<Podcast>()
                            val resultItem = response.body();
                            val entries = resultItem?.results ?: emptyList()
                            entries.forEach { entry ->
                                val podcast = Podcast()
                                podcast.feedUrl = entry.feedUrl
                                podcast.title = entry.trackName
                                podcast.imageUrl = entry.artwork
                                podcast.authors = entry.artistName
                                podcast.trackId = entry.trackId
                                podcasts.add(podcast)

                                /*
                                // test podcasts on db
                                doAsync {
                                    val podcastsFromDb = podcastDao.getPodcasts()
                                    podcast.isInDatabase = podcastsFromDb.value!!.any { cast -> cast.feedId == podcast.feedId }
                                    //podcast.isInDatabase = true
                                }
                                */
                            }

                            if (podcasts.any()) {
                                this.startPosition += podcasts.size
                                callback.invoke(podcasts)
                            }
                            else {
                                invalidate()
                            }
                        }
                    },
                    { throwable ->
                        loadingState.postValue(LoadingState.LOAD_ERR)
                        /*networkState.postValue(NetworkState.error(throwable.message
                                ?: "unknown error"))*/
                    }
            ))

        }
    }

    private fun loadIds(callback: () -> Unit)
    {
        // request Ids
        var request = itunesApi.top("fr", 200, genre);
        request.enqueue(retrofitCallback(
            { response ->
                if (response.isSuccessful) {
                    var podcasts = arrayListOf<Podcast>()
                    var result = response.body()
                    this.ids = result?.resultIds ?: emptyList()
                    callback()
                }
            },
            { throwable ->
                loadingState.postValue(LoadingState.LOAD_ERR)
                //networkState.postValue(NetworkState.error(throwable.message ?: "unknown error"))
            }
        ))
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Podcast>) {
        loadingState.postValue(LoadingState.LOADING)

        val totalCount = 500
        val startPosition = PositionalDataSource.computeInitialLoadPosition(params, totalCount)
        val loadSize = PositionalDataSource.computeInitialLoadSize(params, startPosition, totalCount)

        // request Ids
        loadIds {
            loadInternal(startPosition, loadSize, {podcasts ->
                val state = if (podcasts.isEmpty()) LoadingState.EMPTY else LoadingState.OK
                loadingState.postValue(state)
                callback.onResult(podcasts, startPosition, podcasts.count())
            })
        }
    }

    override fun loadRange(params: LoadRangeParams , callback: LoadRangeCallback<Podcast>)
    {
        val startPosition = params.startPosition
        val pageSize = params.loadSize
        loadingState.postValue(LoadingState.LOADING_MORE)

        loadInternal(startPosition, pageSize, {podcasts ->
            loadingState.postValue(LoadingState.LOAD_MORE_COMPLETE)
            callback.onResult(podcasts)
        })
    }
}