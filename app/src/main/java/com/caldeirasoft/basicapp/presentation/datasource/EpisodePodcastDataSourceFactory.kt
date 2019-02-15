package com.caldeirasoft.basicapp.presentation.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.usecase.GetEpisodesFromFeedlyUseCase

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
    val sourceLiveData = MutableLiveData<EpisodeFeedlyDataSource>()
    val dataProvider = EpisodeFeedlyDataProvider()

    override fun create(): DataSource<Int, Episode> {
        val source: DataSource<Int, Episode> =
                when (section) {
                    null -> {
                        EpisodeFeedlyDataSource(
                                feed = feed,
                                getEpisodesFromFeedlyUseCase = getEpisodesFromFeedlyUseCase,
                                dataProvider = dataProvider)
                                .also {
                                    sourceLiveData.postValue(it)
                                }
                    }
                    else -> {
                        episodeRepository.fetchFactory(section!!).create()
                    }
                }
        return source
    }

    fun applySection(newSection: Int?) {
        this.section = newSection
    }
}