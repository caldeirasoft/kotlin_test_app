package com.caldeirasoft.basicapp.presentation.datasource

import androidx.lifecycle.*
import androidx.paging.*
import com.caldeirasoft.basicapp.domain.entity.Podcast
import com.caldeirasoft.basicapp.data.datasources.remote.ITunesApi
import com.caldeirasoft.basicapp.data.datasources.local.dao.PodcastDao
import com.caldeirasoft.basicapp.domain.repository.ItunesRepository
import com.caldeirasoft.basicapp.domain.repository.PodcastRepository

/**
 * Created by Edmond on 15/02/2018.
 */
class ItunesPodcastDataSourceFactory(
        val itunesRepository: ItunesRepository,
        val podcastRepository: PodcastRepository,
        var genre: Int
) : DataSource.Factory<Int, Podcast>()
{
    // LiveData of Request status.
    private val _sourceLiveData = MutableLiveData<ItunesPodcastDataSource>()
    val sourceLiveData: LiveData<ItunesPodcastDataSource>
        get() = _sourceLiveData

    override fun create(): DataSource<Int, Podcast> {
        val source = ItunesPodcastDataSource(itunesRepository, podcastRepository, genre)
        _sourceLiveData.postValue(source)
        return source
    }

    fun applyGenre(genreValue: Int) {
        genre = genreValue
        invalidate()
    }

    fun invalidate() {
        _sourceLiveData.value?.invalidate()
    }
}