package com.caldeirasoft.basicapp.presentation.datasource

import com.caldeirasoft.castly.domain.model.Episode

/**
 * Created by Edmond on 15/02/2018.
 */
class EpisodeFeedlyDataProvider()
{
    val backingStoreEpisodes= arrayListOf<Episode>()
    var backingStoreContinuation = ""

    fun clear()
    {
        backingStoreEpisodes.clear()
        backingStoreContinuation = ""
    }
}