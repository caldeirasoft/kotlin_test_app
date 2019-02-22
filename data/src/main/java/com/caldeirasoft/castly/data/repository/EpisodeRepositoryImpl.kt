package com.caldeirasoft.castly.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.caldeirasoft.castly.data.datasources.local.dao.EpisodeDao
import com.caldeirasoft.castly.data.extensions.convert
import com.caldeirasoft.castly.data.extensions.convertAll
import com.caldeirasoft.castly.domain.model.*
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

/**
 * Created by Edmond on 15/02/2018.
 */
class EpisodeRepositoryImpl(val episodeDao: EpisodeDao) : EpisodeRepository {

    override fun fetch(feedUrl: String): LiveData<List<Episode>> = episodeDao.fetch(feedUrl).convertAll()

    override fun fetchSync(feedUrl: String): List<Episode> = episodeDao.fetchSync(feedUrl).map { it as Episode }

    override fun fetchFactory(feedUrl: String): DataSource.Factory<Int, Episode> = episodeDao.fetchFactory(feedUrl).convertAll()

    override fun fetch(section: Int): LiveData<List<Episode>> = episodeDao.fetch(section).convertAll()

    override fun fetchSync(section: Int): List<Episode> = episodeDao.fetchSync(section).map { it as Episode }

    override fun fetchQueue(): LiveData<List<Episode>> = episodeDao.fetchQueue().convertAll()

    override fun fetchQueueSync(): List<Episode> = episodeDao.fetchQueueSync()

    override fun fetchFactory(section: Int): DataSource.Factory<Int, Episode> = episodeDao.fetchFactory(section).convertAll()

    override fun fetch(section: Int, feedUrl: String): LiveData<List<Episode>> = episodeDao.fetch(section, feedUrl).convertAll()

    override fun fetchSync(section: Int, feedUrl: String): List<Episode> = episodeDao.fetchSync(section, feedUrl).map { it as Episode }

    override fun fetchFactory(section: Int, feedUrl: String): DataSource.Factory<Int, Episode> = episodeDao.fetchFactory(section, feedUrl).convertAll()

    override fun fetchEpisodesCountByPodcast(section: Int): LiveData<List<PodcastWithCount>> = episodeDao.fetchEpisodesCountByPodcast(section).convertAll()

    override fun fetchEpisodeCountBySection(feedUrl: String): LiveData<SectionWithCount> = episodeDao.fetchEpisodeCountBySection(feedUrl).convert()

    override fun getSync(episodeId: String): Episode? = episodeDao.getSync(episodeId)

    override fun getSync(feedUrl: String, mediaUrl: String): Episode? = episodeDao.getSync(feedUrl, mediaUrl)

    override fun get(episodeId: String): LiveData<Episode> = episodeDao.get(episodeId).convert()

    override fun insert(episode: Episode) {
        episode as EpisodeEntity
        episodeDao.insert(episode)
    }

    override fun update(episode: Episode) {
        episode as EpisodeEntity
        episodeDao.update(episode)
    }

    override fun update(episodes: List<Episode>) {
        episodes as List<EpisodeEntity>
        episodeDao.update(episodes)
    }

    override fun delete(episode: Episode) {
        episode as EpisodeEntity
        episodeDao.delete(episode)
    }

    override fun deleteByPodcast(feedUrl: String) {
        episodeDao.deleteByPodcast(feedUrl)
    }

    /**
     * Insert episode if not exists
     */
    override fun upsert(episode: Episode) {
        getSync(episode.feedUrl, episode.mediaUrl).let {
            if (it != null)
                update(episode)
            else
                insert(episode)
        }
    }
}