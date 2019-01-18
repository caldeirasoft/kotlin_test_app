package com.caldeirasoft.basicapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.caldeirasoft.basicapp.App
import com.caldeirasoft.basicapp.api.feedly.retrofit.FeedlyAPI
import com.caldeirasoft.basicapp.data.db.AppDatabase
import com.caldeirasoft.basicapp.data.db.episodes.EpisodeDataSource
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.entity.PodcastWithCount
import com.caldeirasoft.basicapp.data.entity.SectionWithCount
import com.caldeirasoft.basicapp.data.enum.SectionState
import com.github.salomonbrys.kodein.LazyKodein
import com.github.salomonbrys.kodein.LazyKodeinAware
import com.github.salomonbrys.kodein.instance
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult

/**
 * Created by Edmond on 15/02/2018.
 */
class EpisodeRepository : EpisodeDataSource, LazyKodeinAware {

    private var continuation: String = ""
    private var isAllDataLoaded = false

    override val kodein = LazyKodein { App.context!!.kodein }
    private val database: AppDatabase by kodein.instance()
    private val feedlyAPI: FeedlyAPI by kodein.instance()

    override fun getEpisodeDataSourceFromFeedly(): EpisodeFeedlyDataSourceFactory = EpisodeFeedlyDataSourceFactory(feedlyAPI, database.episodeDao())

    override fun getEpisodeDbDataSource(section: Int): EpisodeDbDataSourceFactory = EpisodeDbDataSourceFactory(database.episodeDao(), section)

    override fun getEpisodeDataSourceFromFake(feed: Podcast): EpisodeFakeDataSourceFactory = EpisodeFakeDataSourceFactory(feed, feedlyAPI, database.episodeDao())

    override fun getPodcastById(feedUrl: String): LiveData<Podcast> = database.podcastDao().getPodcastById(feedUrl)

    override fun getEpisodes(feedUrl: String): LiveData<List<Episode>> = database.episodeDao().getEpisodes(feedUrl)

    override fun fetchEpisodes(feedUrl: String): DataSource.Factory<Int, Episode> = database.episodeDao().fetchEpisodes(feedUrl)

    override fun getEpisodesBySection(section: Int): LiveData<List<Episode>> = database.episodeDao().getEpisodesBySection(section)

    override fun fetchEpisodesBySection(section: Int): DataSource.Factory<Int, Episode> = database.episodeDao().fetchEpisodesBySection(section)

    override fun getEpisodesBySection(section: Int, feedUrl: String): LiveData<List<Episode>> = database.episodeDao().getEpisodesBySection(section, feedUrl)

    override fun fetchEpisodesBySection(section: Int, feedUrl: String): DataSource.Factory<Int, Episode> = database.episodeDao().fetchEpisodesBySection(section, feedUrl)

    override fun fetchEpisodesCoutByPodcast(section: Int): LiveData<List<PodcastWithCount>> = database.episodeDao().fetchEpisodesCoutByPodcast(section)

    override fun fetchEpisodeCountBySection(feedUrl: String): LiveData<SectionWithCount> = database.episodeDao().fetchEpisodeCountBySection(feedUrl)

    override fun getEpisodeById(episodeId: String): Episode? = database.episodeDao().getEpisodeById(episodeId)

    override fun getEpisodeByUrl(feedUrl: String, mediaUrl: String): Episode? = database.episodeDao().getEpisodeByUrl(feedUrl, mediaUrl)

    override fun insertEpisode(episode: Episode) {
        doAsync {
            database.episodeDao().insertEpisode(episode)
        }
    }

    override fun updatePodcast(podcast: Podcast) {
        doAsync {
            database.episodeDao().updatePodcast(podcast)
        }
    }

    override fun updateEpisode(episode: Episode) {
        doAsync {
            database.episodeDao().updateEpisode(episode)
        }
    }

    override fun updateEpisodes(episodes: List<Episode>) {
        doAsync {
            database.episodeDao().updateEpisodes(episodes)
        }
    }

    override fun deleteEpisode(episode: Episode) {
        doAsync {
            database.episodeDao().deleteEpisode(episode)
        }
    }

    override fun deleteEpisodes(feedUrl: String) {
        doAsync {
            database.episodeDao().deleteEpisodes(feedUrl)
        }
    }

    /**
     * Archive episode
     */
    override fun archiveEpisode(episode: Episode) {
        val lastSection = episode.section
        episode.section = SectionState.ARCHIVE.value
        updateEpisode(episode)
        when (lastSection) {
            SectionState.QUEUE.value -> reorderQueue()
        }
    }

    /**
     * Toggle episode favorite status
     */
    override fun toggleEpisodeFavorite(episode: Episode) {
        episode.isFavorite = !episode.isFavorite
        upsertEpisode(episode)
    }

    /**
     * Queue episode first
     */
    override fun queueEpisodeFirst(episode: Episode) {
        // move all episodes from queue down 1 position
        getEpisodesBySection(SectionState.QUEUE.value).value.orEmpty().let {
            when (it.isEmpty()) {
                true -> playEpisode(episode)
                else -> {
                    it.forEachIndexed { i, episode -> if (i > 0) episode.queuePosition = i + 1 }
                    updateEpisodes(it)
                    episode.queuePosition = 1
                    episode.section = SectionState.QUEUE.value
                    upsertEpisode(episode)
                }
            }
        }
    }

    /**
     * Queue episode last
     */
    override fun queueEpisodeLast(episode: Episode) {
        // get queue size
        val queueSize = getEpisodesBySection(SectionState.QUEUE.value).value.orEmpty().size
        episode.section = SectionState.QUEUE.value
        episode.queuePosition = queueSize
        upsertEpisode(episode)
    }

    /**
     * Queue episode last
     */
    override fun playEpisode(episode: Episode) {
        // move all episodes from queue down 1 position
        getEpisodesBySection(SectionState.QUEUE.value).value.orEmpty().let {
            it.forEachIndexed { i, episode -> episode.queuePosition = i + 1 }
            updateEpisodes(it)

            // add episode to first position
            episode.queuePosition = 0
            episode.section = SectionState.QUEUE.value
            upsertEpisode(episode)
        }
    }

    /**
     * Queue episode first
     */
    override fun reorderQueue() {
        // move all episodes from queue down 1 position
        getEpisodesBySection(SectionState.QUEUE.value).value?.let {
            it.forEachIndexed { i, episode -> episode.queuePosition = i }
            updateEpisodes(it)
        }
    }

    /**
     * Insert episode if not exists
     */
    override fun upsertEpisode(episode: Episode) {
        doAsyncResult { getEpisodeByUrl(episode.feedUrl, episode.mediaUrl) }.get().let {
            if (it != null)
                updateEpisode(episode)
            else
                insertEpisode(episode)
        }
    }
}