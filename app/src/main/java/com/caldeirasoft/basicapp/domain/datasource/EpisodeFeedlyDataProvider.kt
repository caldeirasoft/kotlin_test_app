package com.caldeirasoft.basicapp.domain.datasource

import com.caldeirasoft.basicapp.domain.entity.Episode

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