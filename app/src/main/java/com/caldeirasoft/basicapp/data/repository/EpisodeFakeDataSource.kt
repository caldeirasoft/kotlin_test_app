package com.caldeirasoft.basicapp.data.repository

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.*
import com.caldeirasoft.basicapp.Mockup
import com.caldeirasoft.basicapp.data.db.episodes.EpisodeDao
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.api.feedly.retrofit.FeedlyAPI
import com.caldeirasoft.basicapp.ui.common.SingleLiveEvent
import com.caldeirasoft.basicapp.util.LoadingState
import java.util.*

/**
 * Created by Edmond on 15/02/2018.
 */
class EpisodeFakeDataSource(
        val feed: Podcast,
        val feedlyAPI: FeedlyAPI,
        val episodeDao: EpisodeDao
) : PositionalDataSource<Episode>()
{
    //lateinit var episodes: LiveData<List<Episode>>

    var ids:List<String> = arrayListOf<String>()
    var podcasts = arrayListOf<Podcast>()
    val loadingState = MutableLiveData<LoadingState>()
    val durationEvent = SingleLiveEvent<Episode>()
    var continuation = ""
    var isFull: Boolean = false

    private fun loadInternal(pageSize:Int, callback: (List<Episode>) -> Unit, alreadyRetrievedEpisodes: List<Episode> = ArrayList())
    {
        if (isFull)
        {
            callback.invoke(Collections.emptyList())
            return
        }

        val episodes = Mockup.getEpisodesMockup()?.let {
            it.sortedByDescending { episode -> episode.published }.let {
                callback.invoke(it)
            }
        }
        isFull = true
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Episode>) {
        loadingState.postValue(LoadingState.LOADING)

        val totalCount = 200
        val startPosition = PositionalDataSource.computeInitialLoadPosition(params, totalCount)
        val loadSize = PositionalDataSource.computeInitialLoadSize(params, startPosition, totalCount)

        // fetch episodes
        loadInternal(loadSize, {ep:List<Episode> ->

            val state = if (ep.isEmpty()) LoadingState.EMPTY else LoadingState.OK
            loadingState.postValue(state)
            callback.onResult(ep, startPosition)
            Log.d("loadInitial", "startPosition:" + startPosition.toString())
        })
    }

    override fun loadRange(params: LoadRangeParams , callback: LoadRangeCallback<Episode>)
    {
        val startPosition = params.startPosition
        val pageSize = params.loadSize
        loadingState.postValue(LoadingState.LOADING_MORE)

        loadInternal(pageSize, {ep:List<Episode> ->
            val state = LoadingState.LOAD_MORE_COMPLETE
            callback.onResult(ep)
            Log.d("loadRange", "startPosition:" + startPosition.toString())
        })
    }
}