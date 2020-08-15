package com.caldeirasoft.castly.data.repository

import com.caldeirasoft.castly.data.datasources.local.dao.EpisodeDao
import com.caldeirasoft.castly.data.datasources.local.dao.PodcastDao
import com.caldeirasoft.castly.data.datasources.remote.PodcastsApi
import com.caldeirasoft.castly.data.dto.itunes.GenreDto
import com.caldeirasoft.castly.data.dto.itunes.PodcastResultDto
import com.caldeirasoft.castly.data.entity.EpisodeEntity
import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.data.entity.PodcastEntity
import com.caldeirasoft.castly.data.repository.ItunesRepositoryImpl.Companion.DEFAULT_CATEGORY
import com.caldeirasoft.castly.data.util.NetworkBoundDbResource
import com.caldeirasoft.castly.data.util.NetworkBoundFileResource
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

/**
 * Created by Edmond on 15/02/2018.
 */
@kotlinx.coroutines.FlowPreview
@kotlinx.coroutines.ExperimentalCoroutinesApi
class PodcastRepositoryImpl(
        val podcastDao: PodcastDao,
        val episodeDao: EpisodeDao,
        val podcastsApi: PodcastsApi) : PodcastRepository {

    private val TAG = PodcastRepository::class.java.simpleName

    /**
     * Get the cache subscribed podcasts from DB
     */
    override fun fetchSubscribedPodcasts(): Flow<List<Podcast>> = podcastDao.fetchSubscribed()

    /**
     * Gets the cached podcast from database and tries to get
     * fresh podcast from web and save into database
     * if that fails then continues showing cached data.
     */
    override fun getPodcast(podcastId: Long): Flow<Resource<Podcast>> {
        return object : NetworkBoundDbResource<PodcastResultDto, Podcast>() {
            override suspend fun fetchFromNetwork(): Response<PodcastResultDto> =
                    podcastsApi.podcast("143442-3,26", podcastId)

            override fun loadFromDb(): Flow<PodcastEntity> =
                    podcastDao.get(podcastId)

            override suspend fun saveCallResult(response: PodcastResultDto) =
                    savePodcast(podcastId, response)

            override fun shouldFetch(data: Podcast?): Boolean = shouldFetchPodcast(data)
        }.asFlow()
    }

    /**
     * Gets the cached podcast from database and tries to get
     * fresh podcast from web and save into database
     * if that fails then continues showing cached data.
     */
    override fun getPodcast(podcast: Podcast): Flow<Resource<Podcast>> {
        return object : NetworkBoundDbResource<PodcastResultDto, Podcast>(podcast) {
            override suspend fun fetchFromNetwork(): Response<PodcastResultDto> =
                    podcastsApi.podcast("143442-3,26", podcast.id)

            override fun loadFromDb(): Flow<PodcastEntity> =
                    podcastDao.get(podcast.id)

            override suspend fun saveCallResult(response: PodcastResultDto) =
                    savePodcast(podcast.id, response)

            override fun shouldFetch(data: Podcast?): Boolean = shouldFetchPodcast(data)
        }.asFlow()
    }

    /**
     * Select all podcasts from the database
     */
    override fun fetchSync(): List<Podcast> = podcastDao.fetchSync()

    /**
     * Get podcast info
     */
    fun savePodcast(podcastId: Long, response: PodcastResultDto) {
        response.storePlatformData.productDv.results[podcastId]?.let {
            PodcastEntity(podcastId).apply {
                name = it.name
                artistName = it.artistName
                feedUrl = it.feedUrl
                releaseDate = it.releaseDate
                releaseDateTime = it.releaseDateTime
                trackCount = it.trackCount
                artwork = it.artwork.toArtwork()//.replace("{w}x{h}bb.{f}", "400x400bb.jpg")
                description = it.description.standard
                copyright = it.copyright
                contentAdvisoryRating = it.contentRatingsBySystem.riaa.name
                genres = it.genres
                        .filter { genreResult -> genreResult.genreId != DEFAULT_CATEGORY }
                        .map { it.toGenre() }

                podcastsByArtist = response.pageData.moreByArtist
                podcastsListenersAlsoFollow = response.pageData.listenersAlsoBought

                // get podcast from db
                podcastDao.getSync(podcastId)
                        ?.let {
                            // if podcast is in db
                            podcastDao.update(this)
                        }
                        ?: run {
                            // if podcast is not in db
                            podcastDao.insert(this)
                        }

                // get episodes
                it.children.map { kv ->
                    EpisodeEntity(kv.key).also { pe ->
                        kv.value.let { kvp ->
                            pe.name = kvp.name
                            pe.artistName = kvp.artistName
                            pe.artistId = kvp.artistId
                            pe.podcastName = kvp.collectionName
                            pe.podcastId = podcastId
                            pe.genres = kvp.genres.map { GenreDto.toGenre(it) }
                            pe.feedUrl = kvp.feedUrl
                            pe.description = kvp.description.standard
                            pe.releaseDate = kvp.releaseDate
                            pe.releaseDateTime = kvp.releaseDateTime
                            pe.artwork = artwork
                            pe.contentAdvisoryRating = kvp.contentRatingsBySystem.riaa.name
                            pe.podcastEpisodeType = kvp.podcastEpisodeType
                            pe.podcastEpisodeSeason = kvp.podcastEpisodeSeason
                            pe.podcastEpisodeNumber = kvp.podcastEpisodeNumber
                            pe.podcastEpisodeWebsiteUrl = kvp.podcastEpisodeWebsiteUrl
                            kvp.offers.firstOrNull()?.let { offer ->
                                offer.download.let { dl ->
                                    pe.mediaUrl = dl.url
                                }
                                offer.assets.firstOrNull()?.let { asset ->
                                    pe.mediaType = asset.flavor
                                    pe.duration = asset.duration ?: 0
                                }
                            }
                        }
                    }
                }.let {
                    episodeDao.insertIgore(it)
                }
            }
        }
    }

    /**
     * Update new podcasts
     */
    fun shouldFetchPodcast(podcast: Podcast?): Boolean {
        // get podcast from db
        podcast?.let {
            podcastDao.getSync(it.id)
                    ?.let {
                        val trackCount: Int = episodeDao.count(it.id)
                        if ((trackCount == 0) && (it.trackCount > 0)) {
                            return true
                            //TODO: compare date return false
                        }
                    }
        }
        return true
    }
}