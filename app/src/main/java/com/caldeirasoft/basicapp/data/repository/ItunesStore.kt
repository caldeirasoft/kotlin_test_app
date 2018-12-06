package com.caldeirasoft.basicapp.data.repository

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.avast.android.githubbrowser.extensions.retrofitCallback
import com.caldeirasoft.basicapp.api.itunes.retrofit.ITunesAPI
import com.caldeirasoft.basicapp.data.db.podcasts.PodcastDao
import com.caldeirasoft.basicapp.data.entity.Podcast
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.withAlpha
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Edmond on 09/02/2018.
 */
class ItunesStore(
        private var storeFront: String = "",
        val itunesApi: ITunesAPI,
        val podcastDao: PodcastDao)
{
    var liveTrendingPodcasts = MutableLiveData<List<PodcastArtwork>>()
    var livePodcastGroups = MutableLiveData<List<PodcastGroup>>()

    private var podcastsLookup = HashMap<String, Podcast>()
    private var trendingPodcasts = ArrayList<PodcastArtwork>()
    private var podcastGroups = ArrayList<PodcastGroup>()

    fun request() {
        var requestItems = itunesApi.viewGrouping(storeFront);
        requestItems.enqueue(retrofitCallback(
                { response ->
                    if (response.isSuccessful) {
                        val storeResult = response.body();
                        storeResult?.let {
                            // set lockup
                            val lockup = storeResult.storePlatformData.lockup.results
                            lockup.forEach { kvp ->
                                val podcast = Podcast()

                                podcast.trackId = kvp.key.toInt()
                                kvp.value.apply {
                                    if (!feedUrl.isNullOrEmpty())
                                    {
                                        podcast.feedUrl = feedUrl
                                        podcast.authors = artistName
                                        podcast.title = name
                                        if (::artwork.isLateinit) {
                                            podcast.imageUrl = artwork.url.replace("{w}x{h}bb.{f}", "100x100bb.jpg")
                                            podcast.bigImageUrl = artwork.url.replace("{w}x{h}bb.{f}", "400x400bb.jpg")
                                        }
                                        podcastsLookup[kvp.key] = podcast
                                    }
                                }

                            }

                            // set trending header
                            //var header = storeResult.pageData.fcStructure.model.children.first { v -> v.fcKind == 255 && v.token == "allPodcasts" }.children.first()
                            var headerEntries =
                                    storeResult.pageData.fcStructure.model.children.first { v -> v.fcKind == 255 && v.token == "allPodcasts" }.children.first().children.first().children
                            headerEntries.forEach { kvp ->
                                if (kvp.link.type == "content")
                                {
                                    val id = kvp.link.contentId
                                    podcastsLookup[id]?.let { cast ->
                                        PodcastArtwork().apply {
                                            podcast = cast
                                            artworkUrl = kvp.artwork.url.replace("{w}x{h}{c}.{f}", "400x196fa.jpg")
                                            bgColor = Color.parseColor("#" + kvp.artwork.bgColor).withAlpha(210)
                                            textColor = Color.parseColor("#" + kvp.artwork.textColor1)
                                            trendingPodcasts.add(this)
                                        }
                                    }
                                }
                            }

                            // do on background
                            doAsync {
                                // set content
                                var contentGroup =
                                        storeResult.pageData.fcStructure.model.children.first { v -> v.fcKind == 255 && v.token == "allPodcasts" }.children.first().children.filter { v -> v.fcKind == 271 }
                                contentGroup.forEach { kvp ->
                                    val group = PodcastGroup()
                                    group.name = kvp.name

                                    // for each group, get podcasts
                                    var ids = kvp.children.first().content.map { kvp -> kvp.contentId }
                                    val idsJoin = ids.joinToString(",")
                                    var requestLookup = itunesApi.lookup(idsJoin)
                                    val response = requestLookup.execute()
                                    if (response.isSuccessful) {
                                        val podcasts = arrayListOf<Podcast>()
                                        val resultItem = response.body();
                                        val entries = resultItem?.results ?: emptyList()
                                        entries.forEach { entry ->
                                            val podcast = Podcast()
                                            podcast.feedUrl = entry.feedUrl
                                            podcast.title = entry.trackName
                                            podcast.imageUrl = entry.artworkUrl100
                                            podcast.authors = entry.artistName
                                            podcast.trackId = entry.trackId
                                            group.podcasts.add(podcast)
                                        }
                                    }

                                    podcastGroups.add(group)
                                }

                                /*
                                kvp.children.first().content.forEach { kvc ->
                                    val id = kvc.contentId
                                    podcastsLookup[id]?.let { podcast ->
                                        group.podcasts.add(podcast)
                                    }
                                }
                                */
                                liveTrendingPodcasts.postValue(trendingPodcasts)
                                livePodcastGroups.postValue(podcastGroups)
                            }
                        }
                    }
                },
                { throwable ->
                    //loadingState.postValue(LoadingState.LOAD_ERR)
                    /*networkState.postValue(NetworkState.error(throwable.message
                            ?: "unknown error"))*/
                }))
    }
}