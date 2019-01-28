package com.caldeirasoft.basicapp.data.repository

import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.*
import com.avast.android.githubbrowser.extensions.retrofitCallback
import com.caldeirasoft.basicapp.data.db.episodes.EpisodeDao
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.api.feedly.retrofit.FeedlyAPI
import com.caldeirasoft.basicapp.data.enum.SectionState
import com.caldeirasoft.basicapp.ui.common.SingleLiveEvent
import com.caldeirasoft.basicapp.util.LoadingState
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import java.util.*

/**
 * Created by Edmond on 15/02/2018.
 */
class EpisodeFeedlyDataSource(
        val feed: Podcast,
        val feedlyAPI: FeedlyAPI,
        val episodeDao: EpisodeDao,
        val dataProvider: EpisodeFeedlyDataProvider
) : PositionalDataSource<Episode>()
{
    //lateinit var episodes: LiveData<List<Episode>>

    var ids:List<String> = arrayListOf<String>()
    var podcasts = arrayListOf<Podcast>()
    val loadingState = MutableLiveData<LoadingState>()
    val durationEvent = SingleLiveEvent<Episode>()
    var continuation = ""
    var isFull: Boolean = false

    private fun fetchEpisodes(
            pageSize:Int,
            alreadyRetrievedEpisodes: List<Episode> = ArrayList(),
            continuation: String = ""
            ): Deferred<List<Episode>>
    {
        GlobalScope.async {
        if (isFull) {
            Collections.emptyList<Episode>()

        }

        feedlyAPI.getStreamEntries(feed.feedId, pageSize, continuation)
                .enqueue(retrofitCallback(
                        { response ->
                            response.body()?.let { responseEntries ->
                                dataProvider.backingStoreContinuation = responseEntries.continuation ?: ""

                                val episodesList =
                                        responseEntries.items
                                                .filter { entry -> entry.enclosure.firstOrNull()?.href != null }
                                                .map { entry ->
                                                    Episode(entry.id
                                                            , feed.feedUrl
                                                            , entry.title
                                                            , entry.published ?: 0)
                                                            .apply {
                                                                description = entry.summary?.content
                                                                mediaUrl = entry.enclosure.get(0).href
                                                                mediaType = entry.enclosure.get(0).type
                                                                mediaLength = entry.enclosure.get(0).length
                                                                link = entry.origin.htmlUrl
                                                            }
                                                }

                                doAsync {
                                    episodesList.forEach { episode -> retrieveEpisodeDataFromDb(episode, feed) }
                                }

                                val merge = alreadyRetrievedEpisodes.plus(episodesList)
                                if (!dataProvider.backingStoreContinuation.isEmpty()) {
                                    if (episodesList.size == (pageSize - alreadyRetrievedEpisodes.size)) {
                                        callback.invoke(merge)
                                        dataProvider.backingStoreEpisodes.addAll(merge)
                                    }
                                    else {
                                        fetchEpisodes(pageSize, merge, dataProvider.backingStoreContinuation, callback)
                                    }
                                } else {
                                    callback.invoke(merge)
                                    isFull = true
                                }
                            }

                            // if podcast isnt crawled, update crawled + updated
                        },
                        { throwable -> {} }
                ))
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Episode>) {
        loadingState.postValue(LoadingState.LOADING)

        val totalCount = 200
        val startPosition = PositionalDataSource.computeInitialLoadPosition(params, totalCount)
        val loadSize = PositionalDataSource.computeInitialLoadSize(params, startPosition, totalCount)

        // fetch episodes
        fetchEpisodes(
                pageSize = loadSize,
                alreadyRetrievedEpisodes = dataProvider.backingStoreEpisodes,
                continuation = dataProvider.backingStoreContinuation
        ) { eplist ->
            fetchEpisodesCallback(eplist, callback, startPosition)
        }
    }

    override fun loadRange(params: LoadRangeParams , callback: LoadRangeCallback<Episode>)
    {
        val startPosition = params.startPosition
        val pageSize = params.loadSize
        loadingState.postValue(LoadingState.LOADING_MORE)

        fetchEpisodes(
                pageSize = pageSize,
                continuation = dataProvider.backingStoreContinuation
        ) { epList ->
            val state = LoadingState.LOAD_MORE_COMPLETE
            callback.onResult(epList)
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

    private fun fetchEpisodesCallback(ep:List<Episode>, callback: LoadInitialCallback<Episode>, startPosition: Int) {
        val state = if (ep.isEmpty()) LoadingState.EMPTY else LoadingState.OK
        loadingState.postValue(state)
        callback.onResult(ep, startPosition)
    }

    private fun fetchEpisodesCallback(ep:List<Episode>, callback: LoadRangeCallback<Episode>) {
        val state = LoadingState.LOAD_MORE_COMPLETE
        callback.onResult(ep)
    }

    private fun retrieveEpisodeDataFromDb(episode: Episode, feed: Podcast) {
        // get episode in db
        val episodeInDb = episodeDao.getEpisodeById(episode.episodeId)
        episodeInDb.apply {
            this?.apply {
                // get value from db
                episode.section = this.section
                episode.isFavorite = this.isFavorite
                episode.duration = this.duration
                episode.playbackPosition = this.playbackPosition
                episode.queuePosition = this.queuePosition
            }
        } ?: run {
            // if the episode is new / not in db : get from podcast
            // get value from podcast
            episode.section = SectionState.ARCHIVE.value
            episode.imageUrl = feed.imageUrl
            episode.bigImageUrl = feed.bigImageUrl
            episode.podcastTitle = feed.title

            //retrieveEpisodeDuration(episode)
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