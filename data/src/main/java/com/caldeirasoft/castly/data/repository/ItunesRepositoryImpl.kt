package com.caldeirasoft.castly.data.repository

import android.graphics.Color
import android.util.LongSparseArray
import com.caldeirasoft.castly.data.datasources.local.dao.PodcastDao
import com.caldeirasoft.castly.data.datasources.remote.ITunesApi
import com.caldeirasoft.castly.data.datasources.remote.PodcastsApi
import com.caldeirasoft.castly.data.dto.itunes.LookupItemDto
import com.caldeirasoft.castly.data.dto.itunes.MultiRoomResultDto
import com.caldeirasoft.castly.data.dto.itunes.SearchResultDto
import com.caldeirasoft.castly.domain.model.EpisodeEntity
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.model.PodcastArtworkEntity
import com.caldeirasoft.castly.domain.model.PodcastEntity
import com.caldeirasoft.castly.domain.model.itunes.*
import com.caldeirasoft.castly.domain.repository.ItunesRepository

/**
 * Created by Edmond on 15/02/2018.
 */
class ItunesRepositoryImpl(val iTunesAPI: ITunesApi, val podcastsApi: PodcastsApi, val podcastDao: PodcastDao) : ItunesRepository {

    companion object {
        val PODCAST_TOKEN = "allPodcasts"
        val DEFAULT_CATEGORY = 26
    }

    /**
     * Get all podcasts from Lookup query
     */
    override suspend fun lookupAsync(ids: List<Long>): List<Podcast> {
        val idsJoin = ids.joinToString(",")
        val searchResults = iTunesAPI.lookup(idsJoin)
        val entries = searchResults.results
        return entries.map { entry -> getPodcast(entry.trackId, entry) }
    }

    /**
     * Get all podcasts from Lookup query
     */
    override suspend fun lookupAsync(id: Long): Podcast? {
        val searchResults = iTunesAPI.lookup(id.toString())
        val entries = searchResults.results
        return entries.map { entry -> getPodcast(entry.trackId, entry) }.firstOrNull()
    }

    /**
     * Get top podcasts of a category
     */
    override suspend fun topPodcastsAsync(category: Int): List<Podcast> {
        val podcastsList = ArrayList<Podcast>()
        val searchResults = iTunesAPI.topPodcasts("fr", 200, category)
        val entries = searchResults.results
        entries.forEach { entry ->
            val podcast = PodcastEntity(entry.trackId)
            podcast.feedUrl = entry.feedUrl
            podcast.name = entry.trackName
            podcast.artistName = entry.artistName
            podcast.artwork = entry.artworkUrl600
            podcast.artworkWidth = 600
            podcast.artworkHeight = 600
            podcast.releaseDate = entry.releaseDate
            podcast.trackCount = entry.trackCount
            podcast.contentAdvisoryRating = entry.contentAdvisoryRating
            podcastsList.add(podcast)
        }
        return podcastsList
    }


    /**
     * Get top podcasts Ids from a category
     */
    override suspend fun topAsync(category: Int): List<Long> =
            iTunesAPI.top("fr", 200, category).resultIds

    /**
     * Get itunes store main page store front
     */
    override suspend fun getStoreAsync(storeFront: String): StoreData {
        val podcastsLookup = HashMap<Long, Podcast>()
        val trendingPodcasts = ArrayList<PodcastArtwork>()
        val itunesGroups: MutableList<StoreGroup> = arrayListOf()

        val storeResult = iTunesAPI.genre(storeFront, DEFAULT_CATEGORY)
        // set lockup
        val lockup = storeResult.storePlatformData.lockup.results
        lockup.forEach { kvp ->
            kvp.value.apply {
                if (kind == "podcast") {
                    if (feedUrl.isNotEmpty()) {
                        val podcast = PodcastEntity(kvp.key)
                        podcast.name = name
                        podcast.artistName = artistName
                        podcast.feedUrl = feedUrl
                        podcast.releaseDate = releaseDateTime
                        //podcast.trackCount = trackCount
                        if (::artwork.isLateinit) {
                            podcast.artwork = artwork.url
                            podcast.artworkWidth = artwork.width
                            podcast.artworkHeight = artwork.height
                            //podcast.bigImageUrl = artwork.url.replace("{w}x{h}bb.{f}", "400x400bb.jpg")
                        }
                        podcastsLookup[kvp.key] = podcast
                    }
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
                podcastsLookup[id]?.let { cast ->
                    PodcastArtworkEntity(cast).apply {
                        bgColor = Color.parseColor("#" + kvp.artwork.bgColor)//.withAlpha(210)
                        artworkUrl = kvp.artwork.url.replace("{w}x{h}{c}.{f}", "400x196fa.jpg")
                        textColor = Color.parseColor("#" + kvp.artwork.textColor1)
                        trendingPodcasts.add(this)
                    }
                }
            }
        }

        // do on background
        // set content
        val contentGroup =
                storeResult.pageData.fcStructure.model
                        .children.first { v -> v.fcKind == 255 && v.token == PODCAST_TOKEN }
                        .children.first()
                        .children
        contentGroup.forEach { kvp ->
            when (kvp.fcKind) {
                271 -> { // section
                    kvp.children.first().content.let { items ->
                        val section = StoreCollection(
                                kvp.adamId,
                                kvp.name,
                                StoreCollectionType.COLLECTION)

                        section.ids = items.map { kwp -> kwp.contentId.toLong() }
                        section.podcasts = items.mapNotNull { kwp -> podcastsLookup[kwp.contentId] }.take(8)
                        itunesGroups.add(section)
                    }
                }
                261 -> { // collection
                    val multiCollection = StoreMultiCollection(kvp.name)
                    kvp.children.forEach { item ->
                        if (item.link.type == "link" && item.link.target != "internal") {
                            val collection = StoreCollection(
                                    name = item.name,
                                    id = item.adamId).apply {

                                artworkUrl = item.artwork.url.replace("{w}x{h}{c}.{f}", "400x196fa.jpg")
                            }

                            multiCollection.multiCollection.add(collection)

                                    //bgColor = Color.parseColor("#" + item.artwork.bgColor),//.withAlpha(210)
                                    //textColor = Color.parseColor("#" + item.artwork.textColor1))
                        }
                    }
                    if (multiCollection.multiCollection.any()) {
                        itunesGroups.add(multiCollection)
                    }
                }
            }
        }

        val storeData = StoreData(trendingPodcasts, itunesGroups)
        return storeData
    }


    /**
     * Get itunes.apple.com genre page
     */
    override suspend fun getGenreDataAsync(storeFront: String, genreId: Int): StoreData {
        val podcastsLookup = LongSparseArray<Podcast>()
        val trendingPodcasts = ArrayList<PodcastArtwork>()
        val itunesGroups: MutableList<StoreGroup> = arrayListOf()

        val storeResult = iTunesAPI.genre(storeFront, genreId)
        // set lockup
        val lockup = storeResult.storePlatformData.lockup.results
        lockup.forEach { kvp ->
            kvp.value.apply {
                if (kind == "podcast") {
                    getPodcast(kvp.key, this)?.let { podcast ->
                        podcastsLookup.put(kvp.key, podcast)
                    }
                }
            }
        }

        // set trending header
        //var header = storeResult.pageData.fcStructure.model.children.first { v -> v.fcKind == 255 && v.token == "allPodcasts" }.children.first()
        val headerEntries =
                storeResult.pageData.fcStructure.model
                        .children.first { v -> v.fcKind == 255 && v.token == PODCAST_TOKEN }
                        .children.first { v -> v.fcKind == 256 }
                        .children.first { v -> v.fcKind == 258 }
                        .children
        headerEntries.forEach { kvp ->
            if (kvp.link.type == "content") {
                val id = kvp.link.contentId
                podcastsLookup[id]?.let { cast ->
                    PodcastArtworkEntity(cast).apply {
                        bgColor = Color.parseColor("#" + kvp.artwork.bgColor)//.withAlpha(210)
                        artworkUrl = kvp.artwork.url.replace("{w}x{h}{c}.{f}", "400x196fa.jpg")
                        textColor = Color.parseColor("#" + kvp.artwork.textColor1)
                        trendingPodcasts.add(this)
                    }
                }
            }
        }

        // do on background
        // set content
        val contentGroup =
                storeResult.pageData.fcStructure.model
                        .children.first { v -> v.fcKind == 255 && v.token == PODCAST_TOKEN }
                        .children.first { v -> v.fcKind == 256 }
                        .children
        contentGroup.forEach { kvp ->
            when (kvp.fcKind) {
                271 -> { // section
                    kvp.children.first().content.let { items ->
                        val section = StoreCollection(
                                kvp.adamId,
                                kvp.name,
                                StoreCollectionType.COLLECTION)

                        section.ids = items.map { kwp -> kwp.contentId }
                        section.podcasts = items.mapNotNull { kwp -> podcastsLookup[kwp.contentId] }.take(8)

                        //if ((index > 0) && (genre != DEFAULT_CATEGORY)) // genre : masquer le premier groupe
                        //    itunesGroups.add(section)
                    }
                }
                261 -> { // collection
                    val multiCollection = StoreMultiCollection(kvp.name)
                    kvp.children.forEach { item ->
                        if (item.link.type == "link" && item.link.target != "internal") {
                            val collection = StoreCollection(
                                    name = item.name,
                                    id = item.adamId).apply {
                                    //TODO: use regex to define collection type
                                artworkUrl = item.artwork.url.replace("{w}x{h}{c}.{f}", "400x196fa.jpg")
                            }

                            multiCollection.multiCollection.add(collection)

                            //bgColor = Color.parseColor("#" + item.artwork.bgColor),//.withAlpha(210)
                            //textColor = Color.parseColor("#" + item.artwork.textColor1))
                        }
                    }
                    if (multiCollection.multiCollection.any()) {
                        itunesGroups.add(multiCollection)
                    }
                }
            }
        }

        return StoreData(trendingPodcasts, itunesGroups)
    }

    /**
     * Get itunes.apple.com collection data
     */
    override suspend fun getCollectionDataAsync(storeFront: String, id: Int): StoreCollection {
        val podcastsLookup = LongSparseArray<Podcast>()
        var collection: StoreCollection

        val storeResult = iTunesAPI.collection(storeFront, id)
        // set lockup
        val lockup = storeResult.storePlatformData.lockup.results
        lockup.forEach { kvp ->
            kvp.value.apply {
                if (kind == "podcast") {
                    getPodcast(kvp.key, this)?.let { podcast ->
                        podcastsLookup.put(kvp.key, podcast)
                    }
                }
            }
        }

        // do on background
        // set content
        collection = StoreCollection(storeResult.pageData.adamId, storeResult.pageData.pageTitle, StoreCollectionType.COLLECTION).also {
            storeResult.pageData.apply {
                it.podcasts = adamIds.mapNotNull { id -> podcastsLookup[id] }
                if (::uber.isLateinit) {
                    it.artworkUrl = uber.masterArt.lastOrNull()?.url
                    it.type = StoreCollectionType.ROOM
                }
            }
        }

        return collection
    }

    /**
     * Get itunes.apple.com multiroom data
     */
    override suspend fun getMultiRoomDataAsync(storeFront: String, id: Int): StoreMultiCollection {
        val storeResult = iTunesAPI.viewMultiRoom(storeFront, id)
        return getMultiCollectionDataAsync(storeResult)
    }


    /**
     * Get itunes.apple.com feature data
     */
    override suspend fun getFeatureDataAsync(storeFront: String, id: Int): StoreMultiCollection {
        val storeResult = iTunesAPI.viewFeature(storeFront, id)
        return getMultiCollectionDataAsync(storeResult)
    }


    /**
     * Get itunes.apple.com multiroom data
     */
    private fun getMultiCollectionDataAsync(storeResult: MultiRoomResultDto): StoreMultiCollection {
        val podcastsLookup = LongSparseArray<Podcast>()
        var multiCollection: StoreMultiCollection
        // set lockup
        val lockup = storeResult.storePlatformData.lockup.results
        lockup.forEach { kvp ->
            kvp.value.apply {
                if (kind == "podcast") {
                    getPodcast(kvp.key, this)?.let { podcast ->
                        podcastsLookup.put(kvp.key, podcast)
                    }
                }
            }
        }

        // do on background
        // set content
        multiCollection = StoreMultiCollection(storeResult.pageData.pageTitle).also {
            storeResult.pageData.apply {
                if (::uber.isLateinit) {
                    it.artworkUrl = uber.masterArt.lastOrNull()?.url
                }

                segments.forEach { seg ->
                    StoreCollection(seg.adamId, seg.title).let { collection ->
                        collection.podcasts = seg.adamIds.mapNotNull { id -> podcastsLookup[id] }
                        it.multiCollection.add(collection)
                    }
                }
            }
        }

        return multiCollection
    }

    /**
     * Get podcast info
     */
    override suspend fun getPodcastAsync(storeFront: String, id: Long): Podcast? {
        val podcastResult = podcastsApi.podcast(storeFront, id)
        podcastResult.storePlatformData.product_dv.results[id]?.let {
            return PodcastEntity(id).apply {
                name = it.name
                artistName = it.artistName
                 feedUrl = it.feedUrl
                 releaseDate = it.releaseDateTime
                 trackCount = it.trackCount
                 artwork = it.artwork.url//.replace("{w}x{h}bb.{f}", "400x400bb.jpg")
                 artworkWidth = it.artwork.width
                 artworkHeight = it.artwork.height
                description = it.description.standard
                copyright = it.copyright
                 contentAdvisoryRating = it.contentRatingsBySystem.riaa.name

                episodes = it.children.map { kv ->
                    EpisodeEntity(kv.key).apply {
                        name = kv.value.name
                        artistName = kv.value.artistName
                        podcastName = kv.value.collectionName
                        feedUrl = kv.value.feedUrl
                        description = kv.value.description.standard
                        mediaUrl = kv.value.offers.first().download.url
                        mediaType = kv.value.offers.first().assets.first().flavor
                        mediaLength = kv.value.offers.first().assets.first().duration.toLong()
                        artwork = it.artwork.url
                        artworkHeight = it.artwork.height
                        artworkWidth = it.artwork.width
                        contentAdvisoryRating = it.contentRatingsBySystem.riaa.name
                        podcastEpisodeType = it.podcastEpisodeType
                        podcastEpisodeSeason = it.podcastEpisodeSeason
                        podcastEpisodeNumber = it.podcastEpisodeNumber
                        podcastEpisodeWebsiteUrl = it.podcastEpisodeWebsiteUrl
                    }
                }
            }
        }
        return null
    }

    /**
     * Get podcast from a LookupItemDto item
     */
    private fun getPodcast(id:Long, dto: LookupItemDto): Podcast? {
        if (dto.feedUrl.isNotEmpty()) {
            val podcast = PodcastEntity(id)
            podcast.name = dto.name
            podcast.artistName = dto.artistName
            podcast.feedUrl = dto.feedUrl
            podcast.releaseDate = dto.releaseDateTime
            podcast.trackCount = dto.trackCount
            podcast.contentAdvisoryRating = dto.contentRatingsBySystem.riaa.name
            dto.apply {
                if (::artwork.isLateinit) {
                    podcast.artwork = artwork.url
                    podcast.artworkWidth = artwork.width
                    podcast.artworkHeight = artwork.height
                    //podcast.bigImageUrl = artwork.url.replace("{w}x{h}bb.{f}", "400x400bb.jpg")
                }
            }
            return podcast
        }
        return null
    }

    /**
     * Get podcast from a SearchResultDto item
     */
    private fun getPodcast(id:Long, dto: SearchResultDto.Result): Podcast {
        val podcast = PodcastEntity(id)
        podcast.feedUrl = dto.feedUrl
        podcast.name = dto.trackName
        podcast.artistName = dto.artistName
        podcast.artwork = dto.artworkUrl600
        podcast.artworkWidth = 600
        podcast.artworkHeight = 600
        podcast.releaseDate = dto.releaseDate
        podcast.trackCount = dto.trackCount
        podcast.contentAdvisoryRating = dto.contentAdvisoryRating
        return podcast
    }
}