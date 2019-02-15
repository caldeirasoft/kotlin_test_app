package com.caldeirasoft.basicapp.presentation.datasource

import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PositionalDataSource
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.usecase.GetEpisodesFromFeedlyUseCase
import com.caldeirasoft.basicapp.presentation.utils.SingleLiveEvent
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import java.util.*

/**
 * Created by Edmond on 15/02/2018.
 */
class EpisodeFeedlyDataSource(
        val feed: Podcast,
        val getEpisodesFromFeedlyUseCase: GetEpisodesFromFeedlyUseCase,
        val dataProvider: EpisodeFeedlyDataProvider
) : PositionalDataSource<Episode>() {

    data class Result(var data:List<Episode>, var continuation: String)

    // LiveData of Request status.
    val isLoading = MutableLiveData<Boolean>()

    // LiveData of network errors.
    private val _networkErrors = MutableLiveData<String>()
    val networkErrors= _networkErrors as LiveData<String>

    val durationEvent = SingleLiveEvent<Episode>()
    var isFull: Boolean = false

    private fun fetchEpisodes(
            pageSize: Int,
            alreadyRetrievedEpisodes: List<Episode> = ArrayList(),
            continuation: String = ""
    ): Deferred<Result> =
        GlobalScope.async {
            var listEpisodes : List<Episode> = arrayListOf<Episode>()
            if (isFull) {
                //return@async Result(mutableListOf(), "")
            }

            getEpisodesFromFeedlyUseCase.beforeExecute = { isLoading.postValue(true) }
            getEpisodesFromFeedlyUseCase.terminated = { isLoading.postValue(false) }
            getEpisodesFromFeedlyUseCase.failed = { it.printStackTrace() }

            val r = getEpisodesFromFeedlyUseCase
                    .execute(GetEpisodesFromFeedlyUseCase.Params(feed, pageSize, continuation))
                    .await()
                    .data
                    ?.let {

                        if (!it.continuation.isEmpty() &&
                                (it.data.size < pageSize - alreadyRetrievedEpisodes.size)) {
                            val merge = alreadyRetrievedEpisodes.plus(it.data)
                            fetchEpisodes(pageSize, merge, it.continuation).await()
                        } else {
                            Result(it.data, it.continuation)
                        }
                    }
                    ?: Result(mutableListOf(), "")

            dataProvider.backingStoreEpisodes.addAll(r.data)
            dataProvider.backingStoreContinuation = r.continuation
            isFull = r.continuation.isEmpty()

            r
        }


    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Episode>) {
        GlobalScope.launch {
            val totalCount = 200
            val startPosition = PositionalDataSource.computeInitialLoadPosition(params, totalCount)
            val loadSize = PositionalDataSource.computeInitialLoadSize(params, startPosition, totalCount)

            // fetch episodes
            val r = fetchEpisodes(
                    pageSize = loadSize,
                    alreadyRetrievedEpisodes = dataProvider.backingStoreEpisodes,
                    continuation = dataProvider.backingStoreContinuation).await()

            callback.onResult(r.data, startPosition)
        }
    }

    override fun loadRange(params: LoadRangeParams , callback: LoadRangeCallback<Episode>)
    {
        GlobalScope.launch {
            val pageSize = params.loadSize

            val r = fetchEpisodes(
                    pageSize = pageSize,
                    continuation = dataProvider.backingStoreContinuation).await()

            callback.onResult(r.data)
        }
    }

    fun forceInvalidate() {
        dataProvider.clear()
        super.invalidate()
    }

    fun updateEpisodeAndInvalidate(episode: Episode) {
        dataProvider
                .backingStoreEpisodes
                .indexOfFirst { ep -> ep.episodeId == episode.episodeId }
                .let { result ->
                    when (result != -1) {
                        true -> {
                            dataProvider.backingStoreEpisodes[result] = episode
                            invalidate()
                        }
                    }
                }
    }

    private fun retrieveEpisodeDuration(episode: Episode) {
        doAsync {
            try {
                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(episode.mediaUrl, HashMap<String, String>())
                episode.duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
                onComplete {
                    durationEvent.postValue(episode)
                    updateEpisodeAndInvalidate(episode)
                }
            } catch (e: Exception) {
                Log.e("EpisodeFeedlyDataSource", "retrieve media metadata", e)
            }
        }
    }
}