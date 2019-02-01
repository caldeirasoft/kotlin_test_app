package com.caldeirasoft.basicapp.presentation.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import com.caldeirasoft.basicapp.domain.datasource.EpisodeFeedlyDataProvider
import com.caldeirasoft.basicapp.domain.entity.Episode
import com.caldeirasoft.basicapp.domain.entity.Podcast
import com.caldeirasoft.basicapp.domain.repository.EpisodeRepository
import com.caldeirasoft.basicapp.domain.usecase.GetEpisodesFromFeedlyUseCase

/**
 * Created by Edmond on 15/02/2018.
 */
class EpisodePodcastDataSourceFactory (
        val feed: Podcast,
        var section: Int?,
        val episodeRepository: EpisodeRepository,
        val getEpisodesFromFeedlyUseCase: GetEpisodesFromFeedlyUseCase
        ) : DataSource.Factory<Int, Episode>()
{
    var isLoading: LiveData<Boolean> = MutableLiveData()
    val sourceLiveData = MutableLiveData<DataSource<Int, Episode>>()
    val dataProvider = EpisodeFeedlyDataProvider()

    override fun create(): DataSource<Int, Episode> {
        val source: DataSource<Int, Episode> =
                when (section) {
                    null -> {
                        val e = EpisodeFeedlyDataSource(
                                feed = feed,
                                getEpisodesFromFeedlyUseCase = getEpisodesFromFeedlyUseCase,
                                dataProvider = dataProvider)
                        isLoading = Transformations.map(e.isLoading) { it }
                        e
                    }
                    else -> {
                        episodeRepository.fetchEpisodesBySection(section!!).create()
                    }
                }
        sourceLiveData.postValue(source)
        return source
    }

    fun applySection(newSection: Int?) {
        this.section = newSection
    }
}