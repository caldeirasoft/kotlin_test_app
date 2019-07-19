package com.caldeirasoft.castly.data.repository

import android.graphics.Color
import com.caldeirasoft.castly.data.datasources.remote.ITunesApi
import com.caldeirasoft.castly.data.datasources.local.dao.PodcastDao
import com.caldeirasoft.castly.domain.model.*
import com.caldeirasoft.castly.domain.model.itunes.ItunesSection
import com.caldeirasoft.castly.domain.model.itunes.ItunesStore
import com.caldeirasoft.castly.domain.model.itunes.PodcastArtwork
import com.caldeirasoft.castly.domain.repository.ItunesRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

/**
 * Created by Edmond on 15/02/2018.
 */
class ItunesRepositoryImpl(val iTunesAPI: ITunesApi, val podcastDao: PodcastDao) : ItunesRepository {

    companion object {
        val PODCAST_TOKEN = "allPodcasts"
    }

    /**
     * Get all podcasts from Lookup query
     */
    override suspend fun lookup(ids: List<Int>): List<Podcast> {
        val podcastsList = ArrayList<Podcast>()
        val idsJoin = ids.joinToString(",")
        val searchResults = iTunesAPI.lookup(idsJoin)
        val entries = searchResults.results
        entries.forEach { entry ->
            val podcast = PodcastEntity()
            podcast.feedUrl = entry.feedUrl
            podcast.title = entry.trackName
            podcast.imageUrl = entry.artworkUrl100
            podcast.bigImageUrl = entry.artworkUrl600
            podcast.authors = entry.artistName
            podcast.trackId = entry.trackId
            podcastsList.add(podcast)
        }
        return podcastsList
    }

    /**
     * Get top podcasts Ids from a category
     */
    override suspend fun top(category: Int): List<Int> =
            iTunesAPI.top("fr", 200, category).resultIds


    /**
     * Get itunes store front
     */
    override suspend fun getStore(storeFront: String): ItunesStore {
        val mPodcastsLookup = HashMap<String, Podcast>()
        val mTrendingPodcasts = ArrayList<PodcastArtwork>()
        val mItunesSection = ArrayList<ItunesSection>()

        val storeResult = iTunesAPI.viewGrouping(storeFront)
        // set lockup
        val lockup = storeResult.storePlatformData.lockup.results
        lockup.forEach { kvp ->
            val podcast = PodcastEntity()

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
                storeResult.pageData.fcStructure.model.children
                        .first { v -> v.fcKind == 255 && v.token == PODCAST_TOKEN }.children
                        .first().children.first().children
        headerEntries.forEach { kvp ->
            if (kvp.link.type == "content") {
                val id = kvp.link.contentId
                mPodcastsLookup[id]?.let { cast ->
                    PodcastArtworkEntity(cast).apply {
                        bgColor = Color.parseColor("#" + kvp.artwork.bgColor)//.withAlpha(210)
                        artworkUrl = kvp.artwork.url.replace("{w}x{h}{c}.{f}", "400x196fa.jpg")
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
        return store
    }
}