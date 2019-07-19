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
    suspend fun getPodcastFromFeedlyApi(feedUrl: String): Podcast

    /**
     * Get last episode from podcast
     */
    suspend fun getLastEpisode(podcast: Podcast): Episode?

    /**
     * Get stream entries from feed
     */
    suspend fun getStreamEntries(podcast: Podcast, pageSize:Int, continuation: String): FeedlyEntries

    /**
     * Update podcast
     */
    suspend fun updatePodcastFromFeedlyApi(podcast: Podcast): Boolean
}