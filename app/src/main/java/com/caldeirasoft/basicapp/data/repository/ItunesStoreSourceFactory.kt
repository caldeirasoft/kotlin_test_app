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
    companion object {
        val PODCAST_TOKEN = "audioPodcasts"
    }

    var itunesStore = MutableLiveData<ItunesStore>()

    fun request() {
        val mPodcastsLookup = HashMap<String, Podcast>()
        val mTrendingPodcasts = ArrayList<PodcastArtwork>()
        val mItunesSection = ArrayList<ItunesSection>()

        val requestItems = itunesApi.viewGrouping(storeFront)
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
                                    if (!feedUrl.isEmpty()) {
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
                            val headerEntries =
                                    storeResult.pageData.fcStructure.model.children.first { v -> v.fcKind == 255 && v.token == PODCAST_TOKEN }.children.first().children.first().children
                            headerEntries.forEach { kvp ->
                                if (kvp.link.type == "content") {
                                    val id = kvp.link.contentId
                                    mPodcastsLookup[id]?.let { cast ->
                                        PodcastArtwork(cast).apply {
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
                            val contentGroup =
                                    storeResult.pageData.fcStructure.model.children.first { v -> v.fcKind == 255 && v.token == PODCAST_TOKEN }.children.first().children.filter { v -> v.fcKind == 271 }
                            contentGroup.forEach { kvp ->


                                kvp.children.first().content.let { items ->
                                    val section = ItunesSection(
                                            kvp.name,
                                            items.map { kwp -> kwp.contentId.toInt() },
                                            items.map { kwp -> mPodcastsLookup[kwp.contentId] }.filterNotNull().take(6)
                                    )
                                    mItunesSection.add(section)
                                }
                            }

                            val store = ItunesStore(mTrendingPodcasts, mItunesSection)
                            itunesStore.postValue(store)
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