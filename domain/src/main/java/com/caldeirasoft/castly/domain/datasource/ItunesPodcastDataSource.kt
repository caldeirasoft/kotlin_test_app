package com.caldeirasoft.castly.domain.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.repository.ItunesRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import kotlinx.coroutines.runBlocking
import kotlin.math.min

/**
 * Created by Edmond on 15/02/2018.
 */
class ItunesPodcastDataSource(
        private val itunesRepository: ItunesRepository,
        private val podcastRepository: PodcastRepository,
        private val genre: Int
) : BasePagingDataSource<Podcast>()
{
    private var ids:List<Long> = emptyList()

    /**
     * A factory class to initialize the [DataSource.Factory] object.
     */
    class Factory(private val itunesRepository: ItunesRepository,
                private val podcastRepository: PodcastRepository,
                private val genreId: Int)  : DataSource.Factory<Int, Podcast>() {

        val sourceLiveData = MutableLiveData<ItunesPodcastDataSource>()

        override fun create(): DataSource<Int, Podcast> {
            val source = ItunesPodcastDataSource(itunesRepository, podcastRepository, genreId)
            sourceLiveData.postValue(source)
            return source
        }
    }

    override fun loadInitialFromSource(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Podcast>) {
        runBlocking {
            try {
                ids = loadPodcastsIds()

                val podcasts = loadPodcasts(0, params.requestedLoadSize)
                handleLoadInitialSuccess(params, callback, podcasts)
            }
            catch (ex: Exception) {
                handleLoadInitialError(params, callback, ex)
            }
        }
    }

    override fun loadAfterFromSource(params: LoadParams<Int>, callback: LoadCallback<Int, Podcast>) {
        runBlocking {
            try {
                val podcasts = loadPodcasts(params.key, params.requestedLoadSize)
                handleLoadAfterSuccess(params, callback, podcasts, null)
            }
            catch (ex: Exception) {
                handleLoadAfterError(params, callback, ex)
            }
        }
    }

    private suspend fun loadPodcastsIds(): List<Long> =
        itunesRepository.topAsync(genre)


    private suspend fun loadPodcasts(requestedPage:Int, requestedLoadSize:Int): List<Podcast> {
        val podcastsList: ArrayList<Podcast> = arrayListOf()
        ids.let {
            val startPosition = requestedPage * requestedLoadSize
            if (startPosition > it.size - 1) {
                return podcastsList
            }

            val idsSubset = it.subList(startPosition, min(it.size - 1, startPosition + requestedLoadSize))
            // request Podcasts from Ids
            if (idsSubset.isNotEmpty()) {
                itunesRepository.lookupAsync(idsSubset).let { list ->
                    val podcastsFromDb = podcastRepository.fetchSync()
                    list.forEach { item ->
                        podcastsFromDb.firstOrNull { cast -> cast.id == item.id }
                                ?.let { podcastInDb ->
                                    item.isSubscribed = podcastInDb.isSubscribed
                                }
                    }
                    podcastsList.addAll(list)

                }
            }
        }
        return podcastsList
    }
}