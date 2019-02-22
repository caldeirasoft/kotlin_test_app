package com.caldeirasoft.castly.domain.repository;

import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.feedly.FeedlyEntries
import com.caldeirasoft.castly.domain.model.Podcast;
import kotlinx.coroutines.Deferred

/**
 * Created by Edmond on 15/02/2018.
 */

interface FeedlyRepository {

    /**
     * Get podcast from Feedly ID
     */
    fun getPodcastFromFeedlyApi(feedUrl: String): Podcast?

    /**
     * Get last episode from podcast
     */
    fun getLastEpisode(podcast: Podcast): Episode?

    /**
     * Get stream entries from feed
     */
    fun getStreamEntries(podcast: Podcast, pageSize:Int, continuation: String): FeedlyEntries?

    /**
     * Update podcast
     */
    fun updatePodcastFromFeedlyApi(podcast: Podcast): Boolean
}