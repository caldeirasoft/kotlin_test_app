package com.caldeirasoft.castly.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.caldeirasoft.castly.data.datasources.local.dao.EpisodeDao
import com.caldeirasoft.castly.data.extensions.convert
import com.caldeirasoft.castly.data.extensions.convertAll
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.data.entity.EpisodeEntity
import com.caldeirasoft.castly.domain.model.PodcastWithCount
import com.caldeirasoft.castly.domain.model.SectionWithCount
import com.caldeirasoft.castly.domain.repository.EpisodeRepository

/**
 * Created by Edmond on 15/02/2018.
 */
class EpisodeRepositoryImpl(val episodeDao: EpisodeDao) : EpisodeRepository {

    override fun fetch(podcastId: Long): LiveData<List<Episode>> = episodeDao.fetch(podcastId).convertAll()

    override fun fetchSync(podcastId: Long): List<Episode> = episodeDao.fetchSync(podcastId)

    override fun fetchSync(podcastId: Long, limit: Int, offset: Int): List<Episode> = episodeDao.fetchSync(podcastId, limit, offset)

    override fun fetchFactory(podcastId: Long): DataSource.Factory<Int, Episode> = episodeDao.fetchFactory(podcastId).convertAll()

    override fun count(podcastId: Long): Int = episodeDao.count(podcastId)

    override fun fetch(section: Int): LiveData<List<Episode>> = episodeDao.fetch(section).convertAll()

    override fun fetchSync(section: Int): List<Episode> = episodeDao.fetchSync(section)

    override fun fetchQueue(): LiveData<List<Episode>> = episodeDao.fetchQueue().convertAll()

    override fun fetchQueueSync(): List<Episode> = episodeDao.fetchQueueSync()

    override fun fetchFactory(section: Int): DataSource.Factory<Int, Episode> = episodeDao.fetchFactory(section).convertAll()

    override fun fetch(section: Int, podcastId: Long): LiveData<List<Episode>> = episodeDao.fetch(section, podcastId).convertAll()

    override fun fetchSync(section: Int, podcastId: Long): List<Episode> = episodeDao.fetchSync(section, podcastId)

    override fun fetchFactory(section: Int, podcastId: Long): DataSource.Factory<Int, Episode> = episodeDao.fetchFactory(section, podcastId).convertAll()

    override fun fetchEpisodesCountByPodcast(section: Int): LiveData<List<PodcastWithCount>> = episodeDao.fetchEpisodesCountByPodcast(section).convertAll()

    override fun fetchEpisodeCountBySection(podcastId: Long): LiveData<SectionWithCount> = episodeDao.fetchEpisodeCountBySection(podcastId).convert()

    override fun getSync(episodeId: Long): Episode? = episodeDao.getSync(episodeId)

    override fun get(episodeId: Long): LiveData<Episode> = episodeDao.get(episodeId).convert()

    override fun insert(episode: Episode) {
        episode as EpisodeEntity
        episodeDao.insert(episode)
    }

    override fun insertIgore(episodes: List<Episode>) {
        episodes as List<EpisodeEntity>
        episodeDao.insertIgore(episodes)
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

    override fun deleteByPodcast(podcastId: Long) {
        episodeDao.deleteByPodcast(podcastId)
    }

    /**
     * Insert episode if not exists
     */
    override fun upsert(episode: Episode) {
        getSync(episode.id).let {
            if (it != null)
                update(episode)
            else
                insert(episode)
        }
    }
}