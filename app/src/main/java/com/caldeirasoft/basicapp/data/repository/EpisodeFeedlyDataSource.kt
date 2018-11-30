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
import org.jetbrains.anko.doAsync
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by Edmond on 15/02/2018.
 */
class EpisodeFeedlyDataSource(
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

        feedlyAPI.getStreamEntries(feed.feedId, pageSize, continuation)
                .enqueue(retrofitCallback(
                        { response ->
                            response.body()?.let {
                                continuation = it.continuation ?: ""
                                it.items
                                        .filter { entry ->
                                            entry.enclosure.firstOrNull()?.href != null
                                        }
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
                                        .let {
                                            doAsync {
                                                it.forEach { episode ->
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
                                                    } ?: doAsync {
                                                        // if the episode is new / not in db : get from podcast
                                                        // get value from podcast
                                                        episode.section = SectionState.ARCHIVE.value
                                                        episode.imageUrl = feed.imageUrl
                                                        episode.bigImageUrl = feed.bigImageUrl
                                                        episode.podcastTitle = feed.title

                                                        val mmr = MediaMetadataRetriever()
                                                        mmr.setDataSource(episode.mediaUrl, HashMap<String, String>())
                                                        episode.duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()

                                                        durationEvent.postValue(episode)
                                                    }
                                                    //if (episodeInDb == null)
                                                    //   episodeDao.insertEpisode(it)
                                                }
                                            }

                                            val merge = alreadyRetrievedEpisodes.plus(it)
                                            if (!continuation.isEmpty()) {
                                                if (it.size == (pageSize - alreadyRetrievedEpisodes.size))
                                                    callback.invoke(merge)
                                                else {
                                                    loadInternal(pageSize, callback, merge)
                                                }
                                            }
                                            else {
                                                callback.invoke(merge)
                                                isFull = true
                                            }
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