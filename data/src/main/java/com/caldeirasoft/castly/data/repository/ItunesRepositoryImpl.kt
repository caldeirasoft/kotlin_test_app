package com.caldeirasoft.castly.data.repository

import androidx.lifecycle.asFlow
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.caldeirasoft.castly.data.datasources.local.dao.PodcastDao
import com.caldeirasoft.castly.data.datasources.remote.ITunesApi
import com.caldeirasoft.castly.data.datasources.remote.PodcastsApi
import com.caldeirasoft.castly.data.dto.itunes.*
import com.caldeirasoft.castly.data.entity.PodcastEntity
import com.caldeirasoft.castly.data.models.itunes.GroupingPageDataEntity
import com.caldeirasoft.castly.data.models.itunes.MultiRoomPageDataEntity
import com.caldeirasoft.castly.data.util.ItunesTopPodcastsDataSource
import com.caldeirasoft.castly.data.util.NetworkBoundFileResource
import com.caldeirasoft.castly.domain.model.entities.Artwork
import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.domain.model.file.FileManager
import com.caldeirasoft.castly.domain.model.itunes.*
import com.caldeirasoft.castly.domain.repository.ItunesRepository
import com.caldeirasoft.castly.domain.util.FlowPaginationModel
import com.caldeirasoft.castly.domain.util.Resource
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types.newParameterizedType
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.stringify
import kotlinx.serialization.modules.SerializersModule
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import retrofit2.Response

/**
 * Created by Edmond on 15/02/2018.
 */
@kotlinx.coroutines.FlowPreview
@kotlinx.coroutines.ExperimentalCoroutinesApi
class ItunesRepositoryImpl(
        private val iTunesAPI: ITunesApi,
        private val podcastsApi: PodcastsApi,
        private val podcastDao: PodcastDao,
        private val fileManager: FileManager,
        private val marshaller: Json) : ItunesRepository {

    private val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    companion object {
        val PODCAST_TOKEN = "allPodcasts"
        val DEFAULT_CATEGORY = 26
    }

    /**
     * Get itunes.apple.com genre page (grouping_page)
     */
    @UnstableDefault
    override fun getGroupingPageData(storeFront: String, genreId: Int): Flow<Resource<GroupingPageData>> {
        return object : NetworkBoundFileResource<GroupingPageResultDto, GroupingPageData>() {
            override suspend fun fetchFromNetwork(): Response<GroupingPageResultDto> =
                    iTunesAPI.genre(storeFront, genreId)

            override fun loadFromFile(): GroupingPageData? =
                    loadGroupingPageDataFromFile(genreId)

            override suspend fun processResponse(response: GroupingPageResultDto): GroupingPageData =
                    processGroupingData(response, genreId)

            override suspend fun saveCallResult(data: GroupingPageData) =
                    saveGroupingPageData(data, genreId)

            override fun shouldFetch(data: GroupingPageData?): Boolean {
                return true
            }
        }.asFlow()
    }

    /**
     * Get top podcasts data by genre
     */
    override fun getTopPodcastsData(
            clientScope: CoroutineScope,
            pageSize: Int,
            genreId: Int): FlowPaginationModel<Podcast> {
        return object : FlowPaginationModel<Podcast> {
            private val dataSourceFactory =
                    ItunesTopPodcastsDataSource.Factory(
                            clientScope,
                            this@ItunesRepositoryImpl,
                            genreId)

            override val pagedList: Flow<PagedList<Podcast>>
                get() = LivePagedListBuilder(
                        dataSourceFactory, PagedList.Config.Builder()
                        .setPageSize(pageSize)
                        .setEnablePlaceholders(false)
                        .build()
                ).build().asFlow()

            override val getState = dataSourceFactory
                    .dataSource
                    .flatMapLatest { it.getState }

            override fun refresh() {
                clientScope.launch {
                    dataSourceFactory.dataSource.first().refresh()
                }
            }
        }
    }

    /**
     * Process the network request response and convert it into a GroupingPageData object
     */
    private suspend fun processGroupingData(storeResult: GroupingPageResultDto, genreId: Int): GroupingPageData {
        val pageData = GroupingPageDataEntity()
        val mapPodcastLookup: HashMap<Long, Podcast> = hashMapOf()
        val mapArtistLookup: HashMap<Long, ArtistItunes> = hashMapOf()

        // set lockup
        val lockup = storeResult.storePlatformData.lockup.results
        getLookupResults(lockup,
                mapPodcastLookup,
                mapArtistLookup)

        // do on background
        // set content
        val contentGroup =
                storeResult.pageData.fcStructure.model
                        .children.first { v -> v.fcKind == 255 && v.token == PODCAST_TOKEN }
                        .children.first()
                        .children
        contentGroup.forEach { kvp ->
            when (kvp.fcKind) {
                258 -> {
                    val collection = GroupingPageDataEntity.TrendingCollectionEntity(kvp.name, kvp.adamId)
                    collection.isHeader = true

                    kvp.children.forEach { item ->
                        when (item.link.type) {
                            "link" -> // room
                                GroupingPageDataEntity.TrendingRoom(
                                        label = item.link.label,
                                        id = item.adamId,
                                        url = item.link.url,
                                        artwork = item.artwork.toArtwork())
                                        .let { room ->
                                            collection.items.add(room)
                                        }
                            "content" -> // podcast
                                mapPodcastLookup[item.link.contentId]?.let { podcast ->
                                    GroupingPageDataEntity.TrendingPodcast(
                                            label = podcast.name,
                                            id = item.adamId,
                                            podcast = podcast,
                                            url = item.link.url,
                                            artwork = item.artwork.toArtwork())
                                            .let { room ->
                                                collection.items.add(room)
                                            }
                                }
                        }
                        //artworkUrl = item.artwork.url.replace("{w}x{h}{c}.{f}", "400x196fa.jpg")
                    }

                    pageData.items.add(collection)

                }
                271 -> { // podcast collection
                    if (kvp.children.first().type == "normal") {
                        // uniquement les collections normales, pas les classements de popularité
                        kvp.children.first().content.let { items ->
                            val section = PodcastCollection(
                                    label = kvp.name,
                                    id = kvp.adamId,
                                    ids = items.map { kwp -> kwp.contentId })

                            pageData.items.add(section)
                        }
                    }
                }
                261 -> { // trending collection
                    val collection = GroupingPageDataEntity.TrendingCollectionEntity(kvp.name, kvp.adamId)
                    kvp.children.forEach { item ->
                        when (item.link.type) {
                            "link" -> // room
                                GroupingPageDataEntity.TrendingRoom(
                                        label = item.link.label,
                                        id = item.adamId,
                                        url = item.link.url,
                                        artwork = item.artwork.toArtwork())
                                        .let { room ->
                                            collection.items.add(room)
                                        }
                            "content" -> // artist/provider page
                                mapArtistLookup[item.link.contentId]?.let { artistLookup ->
                                    GroupingPageDataEntity.TrendingProviderArtist(
                                            label = artistLookup.artistName,
                                            id = artistLookup.id,
                                            url = artistLookup.url,
                                            artwork = item.artwork.toArtwork(),
                                            artist = artistLookup)
                                            .let { artist ->
                                                collection.items.add(artist)
                                            }
                                }
                        }
                        //artworkUrl = item.artwork.url.replace("{w}x{h}{c}.{f}", "400x196fa.jpg")
                    }
                    pageData.items.add(collection)
                }
            }
        }

        // get missing lookups
        val lockupKeys = mapPodcastLookup.keys
        val collectionPodcastIds = pageData.items
                .filterIsInstance<PodcastCollection>()
                .flatMap { collection -> collection.ids }
        if (!lockupKeys.containsAll(collectionPodcastIds)) {
            val missingKeys = collectionPodcastIds.subtract(lockupKeys)
            val missingPodcasts = lookupAsync(missingKeys.toList())
            missingPodcasts.forEach { mapPodcastLookup.put(it.id, it) }
        }

        // add lookup to podcast collections
        pageData.items
                .filterIsInstance<PodcastCollection>()
                .forEach { podcastCollection ->
                    podcastCollection.items.addAll(
                            podcastCollection.ids
                                    .mapNotNull { mapPodcastLookup[it] })
                }

        return pageData
    }

    /**
     * Save grouping page data into a file
     */
    private fun saveGroupingPageData(pageData: GroupingPageData, genreId: Int) {
        val filename = "${genreId}.json";
        val pageDataString = marshaller.stringify(GroupingPageData.serializer(), pageData)
        fileManager.writeFile(filename, pageDataString)
    }

    /**
     * Retrieve grouping data from file
     */
    @kotlinx.serialization.UnstableDefault
    private fun loadGroupingPageDataFromFile(genreId: Int): GroupingPageData? {
        val filename = "${genreId}.json";
        val pageDataFile = fileManager.readFile(filename)
        if (pageDataFile.exists()) {
            fileManager.readFile(pageDataFile)?.let { string ->
                marshaller.parse(GroupingPageDataEntity.serializer(), string).let {
                    return it
                }
            }
        }
        return null
    }

    /**
     * Get all podcasts from Lookup query
     */
    override suspend fun lookupAsync(ids: List<Long>): List<Podcast> {
        val idsJoin = ids.joinToString(",")
        val searchResults = iTunesAPI.lookup(idsJoin)
        val entries = searchResults.body()?.results
        return entries?.map { entry -> getPodcast(id = entry.trackId, dto = entry) }.orEmpty()
    }

    /**
     * Get all podcasts from Lookup query
     */
    override suspend fun lookupAsync(id: Long): Podcast? {
        val searchResults = iTunesAPI.lookup(id.toString())
        val entries = searchResults.body()?.results
        return entries?.map { entry -> getPodcast(entry.trackId, entry) }?.firstOrNull()
    }

    /**
     * Get top podcasts of a category
     */
    override suspend fun topPodcastsAsync(category: Int): List<Podcast> {
        val searchResults = iTunesAPI.topPodcasts("fr", 200, category)
        val entries = searchResults.body()?.results
        return entries?.map { entry -> getPodcast(id = entry.trackId, dto = entry) }.orEmpty()
    }


    /**
     * Get top podcasts Ids from a category
     */
    override suspend fun topAsync(category: Int): List<Long> =
            iTunesAPI.top("fr", 200, category).body()?.resultIds.orEmpty()

    /**
     * Get itunes.apple.com collection data
     */
    override suspend fun getRoomDataAsync(storeFront: String, url: String): RoomPageData {
        val roomData: RoomPageData = RoomPageData()

        val storeResult = iTunesAPI.viewRoom(storeFront, url)
        // set lockup
        val lockup = storeResult.storePlatformData.lockup.results
        getLookupResults(lockup, roomData.mapPodcastLookup)

        // set content
        roomData.apply {
            storeResult.pageData.let {
                pageTitle = it.pageTitle
                description = it.description
                artwork = it.uber?.masterArt?.firstOrNull()?.toArtwork()
                ids = it.adamIds

                // get remaining lookups
                val lockupKeys = lockup.keys
                if (!lockupKeys.containsAll(ids)) {
                    val missingKeys = ids.toLongArray().subtract(lockupKeys)
                    val missingPodcasts = lookupAsync(missingKeys.toList())
                    missingPodcasts.forEach { mapPodcastLookup.put(it.id, it) }
                }

                // add all podcasts
                it.adamIds.mapNotNull { mapPodcastLookup[it] }.let { list ->
                    this.addAll(list)
                }
            }
        }

        return roomData
    }

    /**
     * Get itunes.apple.com multiroom data
     */
    override suspend fun getMultiRoomDataAsync(storeFront: String, url: String): MultiRoomPageData {
        val roomData: MultiRoomPageData = MultiRoomPageDataEntity()
        val storeResult = iTunesAPI.viewMultiRoom(storeFront, url)
        // set lockup
        val lockup = storeResult.storePlatformData.lockup.results
        getLookupResults(lockup, roomData.mapPodcastLookup)

        // set content
        roomData.apply {
            storeResult.pageData.let {
                pageTitle = it.pageTitle
                description = it.description
                artwork = it.uber?.masterArt?.firstOrNull()?.toArtwork()

                it.segments.map { segment ->
                    PodcastCollection(
                            label = segment.title,
                            id = segment.adamId,
                            ids = segment.adamIds
                    )
                }.let {
                    roomData.addAll(it)
                }
            }
        }

        return roomData
    }

    /**
     * Get artist data URL
     */
    override suspend fun getArtistPageDataAsync(storeFront: String, artistId: Long): ArtistPageData {
        // set content
        return ArtistPageData().also { artistPageData ->
            podcastsApi.artist(storeFront, artistId).let { storeResult ->
                // set lockup
                storeResult.storePlatformData.lockup?.results?.let { lockup ->
                    this.getLookupResults(lockup, artistPageData.mapPodcastLookup)
                }

                storeResult.pageData.let {
                    artistPageData.artist = ArtistItunes().apply {
                        artistName = it.artist.name
                        id = it.artist.adamId
                        artwork = it.uber?.masterArt?.lastOrNull()?.toArtwork()
                    }

                    it.contentData.map { content ->
                        when (content.dkId) {
                            null ->
                                PodcastCollection(
                                        label = content.title,
                                        id = content.chunkId ?: 0,
                                        ids = content.adamIds
                                )
                            else -> null
                        }
                    }.let { list ->
                        artistPageData.addAll(list.filterNotNull())
                    }
                }
            }
        }
    }

    /**
     * Get podcast from a SearchResultDto item
     */
    private fun getPodcast(id: Long, dto: SearchResultDto.Result): Podcast {
        return PodcastEntity(id).apply {
            feedUrl = dto.feedUrl
            name = dto.trackName
            artistName = dto.artistName
            artwork = Artwork(dto.artworkUrl600, 600, 600)
            releaseDate = dto.releaseDate.toLocalDate()
            releaseDateTime = dto.releaseDate
            trackCount = dto.trackCount
            contentAdvisoryRating = dto.contentAdvisoryRating
        }
    }

    /**
     * Get lookup results and store then into hashmaps
     */
    private fun getLookupResults(
            results: Map<Long, LookupItemDto>,
            mapPodcastLookup: HashMap<Long, Podcast>,
            mapArtistLookup: HashMap<Long, ArtistItunes>? = null) {

        results.forEach { kvp ->
            kvp.value.let { dto ->
                when (dto.kind) {
                    "podcast" ->
                        PodcastEntity(id = kvp.key).let {
                            it.name = dto.name
                            it.artistName = dto.artistName
                            it.artistId = dto.artistId
                            it.genres = dto.genres.map { GenreDto.toGenre(it) }
                            it.feedUrl = dto.feedUrl

                            it.releaseDate = dto.releaseDate ?: LocalDate.MIN
                            it.releaseDateTime = dto.releaseDateTime ?: LocalDateTime.MIN
                            //it.podcastType = podcastType
                            it.userRating = dto.userRating?.getRating() ?: 0F
                            it.artwork = dto.artwork?.let { artwork ->
                                ArtworkDto.toArtwork(artwork)
                            }

                            mapPodcastLookup.put(kvp.key, it)
                        }
                    "artist" ->
                        ArtistItunes().let {
                            it.artistName = dto.name
                            it.id = dto.id
                            it.url = dto.url
                            it.artwork = dto.editorialArtwork?.storeFlowcase?.let { artwork ->
                                ArtworkDto.toArtwork(artwork)
                            }
                            it.genres = dto.genres.map { GenreDto.toGenre(it) }
                            mapArtistLookup?.put(kvp.key, it)
                        }
                    else -> {
                    }
                }
            }
        }

    }
}