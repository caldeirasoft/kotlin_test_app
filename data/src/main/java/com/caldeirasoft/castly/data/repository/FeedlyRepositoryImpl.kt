package com.caldeirasoft.castly.data.repository

import com.caldeirasoft.castly.data.datasources.remote.FeedlyApi
import com.caldeirasoft.castly.data.dto.feedly.EntryDto
import com.caldeirasoft.castly.data.dto.feedly.StreamEntriesDto
import com.caldeirasoft.castly.domain.model.*
import com.caldeirasoft.castly.domain.model.feedly.FeedlyEntries
import com.caldeirasoft.castly.domain.repository.FeedlyRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

/**
 * Created by Edmond on 15/02/2018.
 */
class FeedlyRepositoryImpl(
        val feedlyApi: FeedlyApi
) : FeedlyRepository {
    /**
     * Select a podcast by id
     */
    override fun getPodcastFromFeedlyApi(feedUrl: String): Deferred<Podcast?> =
    // request Ids
            GlobalScope.async {
                val feedId = "feed/" + feedUrl
                val feed = feedlyApi.getFeed(feedId).await();
                val podcast = PodcastEntity()
                //podcast.imageUrl = feed.artwork
                podcast.feedUrl = feedUrl
                podcast.updated = feed.updated
                podcast.description = feed.description
                podcast
            }

    /**
     * Get stream entries from feed
     */
    override fun getStreamEntries(podcast: Podcast, pageSize: Int, continuation: String): Deferred<FeedlyEntries> =
            GlobalScope.async {
                val responseEntries = feedlyApi.getStreamEntries(podcast.feedId, pageSize, continuation).await()
                val feedlyEntries = FeedlyEntries(
                        data = getEpisodesFromEntries(podcast, responseEntries),
                        continuation = responseEntries.continuation)
                feedlyEntries
            }

    /**
     * Get episodes from stream entries
     */
    private fun getEpisodesFromEntries(podcast: Podcast, entries: StreamEntriesDto): List<Episode> =
            entries.items
                    .filter { entry -> entry.enclosure.firstOrNull()?.href != null }
                    .map { entry ->
                        getEpisodeFromEntry(entry, podcast)
                    }

    /**
     * Get last episode of a podcast
     */
    override fun getLastEpisode(podcast: Podcast): Deferred<Episode?> =
            GlobalScope.async {
                val streamEntries = feedlyApi.getStreamEntries(podcast.feedId, 1, "").await()
                streamEntries.items
                        .filter { entry ->
                            entry.enclosure.firstOrNull()?.href != null
                        }
                        .map { entry ->
                            getEpisodeFromEntry(entry, podcast)
                        }
                        .firstOrNull()
            }

    /**
     * Update podcast
     */
    override fun updatePodcastFromFeedlyApi(podcast: Podcast): Deferred<Boolean> =
            GlobalScope.async {
                val result = getPodcastFromFeedlyApi(podcast.feedUrl).await()
                result?.let {
                    podcast.updated = it.updated
                    podcast.description = it.description
                    true
                } ?: false
            }


    /**
     * Get episode from stream entry
     */
    private fun getEpisodeFromEntry(entry: EntryDto, podcast: Podcast): Episode =
            EpisodeEntity(entry.id
                    , entry.originId ?: ""
                    , entry.title
                    , entry.published ?: 0)
                    .apply {
                        description = entry.summary?.content
                        mediaUrl = entry.enclosure.get(0).href
                        mediaType = entry.enclosure.get(0).type
                        mediaLength = entry.enclosure.get(0).length
                        link = entry.origin.htmlUrl
                        podcastTitle = podcast.title
                        imageUrl = podcast.imageUrl
                        bigImageUrl = podcast.bigImageUrl
                    }

}