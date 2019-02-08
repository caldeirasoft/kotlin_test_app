package com.caldeirasoft.basicapp.domain.repository;

import com.caldeirasoft.basicapp.data.dto.feedly.EntryDto
import com.caldeirasoft.basicapp.data.dto.feedly.StreamEntriesDto
import com.caldeirasoft.basicapp.domain.entity.Episode
import com.caldeirasoft.basicapp.domain.entity.Podcast;
import kotlinx.coroutines.Deferred

/**
 * Created by Edmond on 15/02/2018.
 */

interface FeedlyRepository {

    /**
     * Get podcast from Feedly ID
     */
    fun getPodcastFromFeedlyApi(feedUrl: String): Deferred<Podcast?>

    /**
     * Get last episode from podcast
     */
    fun getLastEpisode(podcast: Podcast): Deferred<Episode?>

    /**
     * Get stream entries from feed
     */
    fun getStreamEntries(podcast: Podcast, pageSize:Int, continuation: String): Deferred<StreamEntriesDto>

    /**
     * Get episodes from stream entries
     */
    fun getEpisodesFromEntries(podcast: Podcast, entries: StreamEntriesDto): List<Episode>

    /**
     * Update podcast
     */
    fun updatePodcastFromFeedlyApi(podcast: Podcast): Deferred<Boolean>

    /**
     * Get episode from stream entry
     */
    fun getEpisodeFromEntry(entry: EntryDto, podcast: Podcast): Episode
}