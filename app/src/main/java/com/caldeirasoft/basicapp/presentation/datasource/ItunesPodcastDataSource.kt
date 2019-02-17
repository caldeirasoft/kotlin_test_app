package com.caldeirasoft.basicapp.presentation.datasource

import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.repository.ItunesRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by Edmond on 15/02/2018.
 */
class ItunesPodcastDataSource(
        itunesRepository: ItunesRepository,
        podcastRepository: PodcastRepository,
        val genre: Int
) : ItunesBaseDataSource(itunesRepository = itunesRepository, podcastRepository = podcastRepository)
{
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Podcast>) {
        GlobalScope.launch {
            try {
                _isLoading.postValue(true)
                // request Ids
                this@ItunesPodcastDataSource.ids = itunesRepository.top(genre).await()
                // request podcasts
                val podcasts = loadPodcasts(0, 1, params.requestedLoadSize).await()
                callback.onResult(podcasts, null, 1)
            }
            catch (e:Throwable) {
                _networkErrors.postValue(e.message)
            }
            finally {
                _isLoading.postValue(false)
            }
        }
    }
}