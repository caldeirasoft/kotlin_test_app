package com.caldeirasoft.basicapp.data.repository

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import com.avast.android.githubbrowser.extensions.retrofitCallback
import com.caldeirasoft.basicapp.api.itunes.retrofit.ITunesAPI
import com.caldeirasoft.basicapp.data.db.podcasts.PodcastDao
import com.caldeirasoft.basicapp.data.entity.Podcast
import org.jetbrains.anko.withAlpha
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Edmond on 09/02/2018.
 */
class ItunesStoreSourceFactory(
        private var storeFront: String = "",
        val itunesApi: ITunesAPI,
        val podcastDao: PodcastDao)
{
    var trendingPodcasts = MutableLiveData<List<PodcastArtwork>>()
    var itunesSections = MutableLiveData<List<ItunesSection>>()

    private var mPodcastsLookup = HashMap<String, Podcast>()
    private var mTrendingPodcasts = ArrayList<PodcastArtwork>()
    private var mItunesSection = ArrayList<ItunesSection>()

    fun request() {
        var requestItems = itunesApi.viewGrouping(storeFront)
        requestItems.enqueue(retrofitCallback(
                { response ->
                    if (response.isSuccessful) {
                        val storeResult = response.body()
                        storeResult?.let {
                            // set lockup
                            val lockup = storeResult.storePlatformData.lockup.results
                            lockup.forEach { kvp ->
                                val podcast = Podcast()

                                podcast.trackId = kvp.key.toInt()
                                kvp.value.apply {
                                    if (!feedUrl.isNullOrEmpty()) {
                                        podcast.feedUrl = feedUrl
                                        podcast.authors = artistName
                                        podcast.title = name
                                        if (::artwork.isLateinit) {
                                            podcast.imageUrl = artwork.url.replace("{w}x{h}bb.{f}", "100x100bb.jpg")
                                            podcast.bigImageUrl = artwork.url.replace("{w}x{h}bb.{f}", "400x400bb.jpg")
                                        }
                                        mPodcastsLookup[kvp.key] = podcast
                                    }
                                }

                            }

                            // set trending header
                            //var header = storeResult.pageData.fcStructure.model.children.first { v -> v.fcKind == 255 && v.token == "allPodcasts" }.children.first()
                            var headerEntries =
                                    storeResult.pageData.fcStructure.model.children.first { v -> v.fcKind == 255 && v.token == "allPodcasts" }.children.first().children.first().children
                            headerEntries.forEach { kvp ->
                                if (kvp.link.type == "content") {
                                    val id = kvp.link.contentId
                                    mPodcastsLookup[id]?.let { cast ->
                                        PodcastArtwork().apply {
                                            podcast = cast
                                            artworkUrl = kvp.artwork.url.replace("{w}x{h}{c}.{f}", "400x196fa.jpg")
                                            bgColor = Color.parseColor("#" + kvp.artwork.bgColor).withAlpha(210)
                                            textColor = Color.parseColor("#" + kvp.artwork.textColor1)
                                            mTrendingPodcasts.add(this)
                                        }
                                    }
                                }
                            }

                            // do on background
                            // set content
                            var contentGroup =
                                    storeResult.pageData.fcStructure.model.children.first { v -> v.fcKind == 255 && v.token == "allPodcasts" }.children.first().children.filter { v -> v.fcKind == 271 }
                            contentGroup.forEach { kvp ->

                                val section = ItunesSection(kvp.name, kvp.children.first().content.map { kwp -> kwp.contentId.toInt() })
                                mItunesSection.add(section)
                            }

                            trendingPodcasts.postValue(mTrendingPodcasts)
                            itunesSections.postValue(mItunesSection)

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