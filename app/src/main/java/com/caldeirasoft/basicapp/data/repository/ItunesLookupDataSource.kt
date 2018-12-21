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
import kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.DeclaredMemberIndex

/**
 * Created by Edmond on 15/02/2018.
 */
class ItunesLookupDataSource(
        val itunesApi: ITunesAPI,
        val podcastDao: PodcastDao,
        val ids:List<Int>
) : PageKeyedDataSource<Int, Podcast>()
{
    var startPosition:Int = 0
    val loadingState = MutableLiveData<LoadingState>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Podcast>) {
        loadingState.postValue(LoadingState.LOADING)
        // request Ids
        loadInternal(0, 1, params.requestedLoadSize, initialCallback = callback, callback = null)
    }

    override fun loadAfter(params: LoadParams<Int> , callback: LoadCallback<Int, Podcast>)
    {
        val page = params.key
        loadingState.postValue(LoadingState.LOADING_MORE)

        loadInternal(page, page + 1, params.requestedLoadSize, initialCallback = null, callback = callback)
    }

    override fun loadBefore(params: LoadParams<Int> , callback: LoadCallback<Int, Podcast>) {
    }

    fun loadInternal(requestedPage:Int, adjacentPage:Int, requestedLoadSize:Int
                     , initialCallback: LoadInitialCallback<Int, Podcast>?
                     , callback: LoadCallback<Int, Podcast>?)
    {
        val startPosition = requestedPage * requestedLoadSize
        if (startPosition > ids.size - 1) {
            callback?.let {
                loadingState.postValue(LoadingState.LOAD_MORE_COMPLETE)
                it.onResult(ArrayList(), adjacentPage)
            }
            return
        }
        val idsSubset = ids.subList(startPosition, Math.min(ids.size - 1, startPosition + requestedLoadSize))

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
                                podcast.imageUrl = entry.artworkUrl100
                                podcast.bigImageUrl = entry.artworkUrl600
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

                            val state = if (podcasts.isEmpty()) LoadingState.EMPTY else LoadingState.OK
                            loadingState.postValue(state)
                            if (podcasts.any()) {
                                //this.startPosition += podcasts.size
                                initialCallback?.let {
                                    loadingState.postValue(LoadingState.OK)
                                    it.onResult(podcasts, null, adjacentPage)
                                }
                                callback?.let {
                                    loadingState.postValue(LoadingState.LOAD_MORE_COMPLETE)
                                    it.onResult(podcasts, adjacentPage)
                                }
                            }
                            else {
                                initialCallback?.let {
                                    loadingState.postValue(LoadingState.EMPTY)
                                }
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
}