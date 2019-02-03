package com.caldeirasoft.basicapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.caldeirasoft.basicapp.data.datasources.local.dao.EpisodeDao
import com.caldeirasoft.basicapp.domain.repository.EpisodeRepository
import com.caldeirasoft.basicapp.domain.entity.Episode
import com.caldeirasoft.basicapp.domain.entity.Podcast
import com.caldeirasoft.basicapp.domain.entity.PodcastWithCount
import com.caldeirasoft.basicapp.domain.entity.SectionWithCount
import com.caldeirasoft.basicapp.domain.entity.SectionState
import kotlinx.coroutines.*
import org.jetbrains.anko.doAsyncResult

/**
 * Created by Edmond on 15/02/2018.
 */
class EpisodeRepositoryImpl(val episodeDao: EpisodeDao) : EpisodeRepository {

    override fun getEpisodes(feedUrl: String): LiveData<List<Episode>> = episodeDao.getEpisodes(feedUrl)

    override fun fetchEpisodes(feedUrl: String): DataSource.Factory<Int, Episode> = episodeDao.fetchEpisodes(feedUrl)

    override fun getEpisodesBySection(section: Int): LiveData<List<Episode>> = episodeDao.getEpisodesBySection(section)

    override fun fetchEpisodesBySection(section: Int): DataSource.Factory<Int, Episode> = episodeDao.fetchEpisodesBySection(section)

    override fun getEpisodesBySection(section: Int, feedUrl: String): LiveData<List<Episode>> = episodeDao.getEpisodesBySection(section, feedUrl)

    override fun fetchEpisodesBySection(section: Int, feedUrl: String): DataSource.Factory<Int, Episode> = episodeDao.fetchEpisodesBySection(section, feedUrl)

    override fun fetchEpisodesCoutByPodcast(section: Int): LiveData<List<PodcastWithCount>> = episodeDao.fetchEpisodesCoutByPodcast(section)

    override fun fetchEpisodeCountBySection(feedUrl: String): LiveData<SectionWithCount> = episodeDao.fetchEpisodeCountBySection(feedUrl)

    override fun getEpisodeById(episodeId: String): Episode? = episodeDao.getEpisodeById(episodeId)

    override fun getEpisodeByUrl(feedUrl: String, mediaUrl: String): Episode? = episodeDao.getEpisodeByUrl(feedUrl, mediaUrl)

    override fun getEpisodeLive(episodeId:String): LiveData<Episode> = episodeDao.getEpisodeLive(episodeId)

    override suspend fun insertEpisode(episode: Episode) {
        withContext(Dispatchers.IO){
            episodeDao.insertEpisode(episode)
        }
    }

    override suspend fun updatePodcast(podcast: Podcast) {
        withContext(Dispatchers.IO){
            episodeDao.updatePodcast(podcast)
        }
    }

    override suspend fun updateEpisode(episode: Episode) {
        withContext(Dispatchers.IO){
            episodeDao.updateEpisode(episode)
        }
    }

    override suspend fun updateEpisodes(episodes: List<Episode>) {
        withContext(Dispatchers.IO) {
            episodeDao.updateEpisodes(episodes)
        }
    }

    override suspend fun deleteEpisode(episode: Episode) {
        withContext(Dispatchers.IO){
            episodeDao.deleteEpisode(episode)
        }
    }

    override suspend fun deleteEpisodes(feedUrl: String) {
        withContext(Dispatchers.IO){
            episodeDao.deleteEpisodes(feedUrl)
        }
    }

    /**
     * Archive episode
     */
    override suspend fun archiveEpisode(episode: Episode) {
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
    override suspend fun toggleEpisodeFavorite(episode: Episode) {
        episode.isFavorite = !episode.isFavorite
        upsertEpisode(episode)
    }

    /**
     * Queue episode first
     */
    override suspend fun queueEpisodeFirst(episode: Episode) {
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
    override suspend fun queueEpisodeLast(episode: Episode) {
        // get queue size
        val queueSize = getEpisodesBySection(SectionState.QUEUE.value).value.orEmpty().size
        episode.section = SectionState.QUEUE.value
        episode.queuePosition = queueSize
        upsertEpisode(episode)
    }

    /**
     * Queue episode last
     */
    override suspend fun playEpisode(episode: Episode) {
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
    override suspend fun reorderQueue() {
        // move all episodes from queue down 1 position
        getEpisodesBySection(SectionState.QUEUE.value).value?.let {
            it.forEachIndexed { i, episode -> episode.queuePosition = i }
            updateEpisodes(it)
        }
    }

    /**
     * Insert episode if not exists
     */
    override suspend fun upsertEpisode(episode: Episode) {
        GlobalScope.launch {
            getEpisodeByUrl(episode.feedUrl, episode.mediaUrl).let {
                if (it != null)
                    updateEpisode(episode)
                else
                    insertEpisode(episode)
            }
        }
    }
}