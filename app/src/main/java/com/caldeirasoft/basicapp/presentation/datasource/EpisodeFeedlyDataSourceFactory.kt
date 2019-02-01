package com.caldeirasoft.basicapp.presentation.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.caldeirasoft.basicapp.domain.datasource.EpisodeFeedlyDataProvider
import com.caldeirasoft.basicapp.domain.entity.Episode
import com.caldeirasoft.basicapp.domain.entity.Podcast
import com.caldeirasoft.basicapp.domain.usecase.GetEpisodesFromFeedlyUseCase

/**
 * Created by Edmond on 15/02/2018.
 */
class EpisodeFeedlyDataSourceFactory (
        val feed: Podcast,
        val getEpisodesFromFeedlyUseCase: GetEpisodesFromFeedlyUseCase
        ) : DataSource.Factory<Int, Episode>()
{
    val sourceLiveData = MutableLiveData<EpisodeFeedlyDataSource>()
    val dataProvider = EpisodeFeedlyDataProvider()

    override fun create(): DataSource<Int, Episode> {
        val source = EpisodeFeedlyDataSource(
                feed = feed,
                getEpisodesFromFeedlyUseCase = getEpisodesFromFeedlyUseCase,
                dataProvider = dataProvider)
        sourceLiveData.postValue(source)
        return source
    }
}