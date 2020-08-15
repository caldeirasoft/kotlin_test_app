package com.caldeirasoft.castly.data.repository

import com.caldeirasoft.castly.data.datasources.local.dao.EpisodeDao
import com.caldeirasoft.castly.domain.model.entities.Episode
import com.caldeirasoft.castly.data.entity.EpisodeEntity
import com.caldeirasoft.castly.domain.model.entities.PodcastWithCount
import com.caldeirasoft.castly.domain.model.entities.SectionWithCount
import com.caldeirasoft.castly.domain.model.itunes.PodcastEpisodeItunes
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import kotlinx.coroutines.flow.Flow

/**
 * Created by Edmond on 15/02/2018.
 */
class EpisodeRepositoryImpl(val episodeDao: EpisodeDao) : EpisodeRepository {

    override fun fetch(podcastId: Long): Flow<List<Episode>> = episodeDao.fetch(podcastId)

    override fun fetchSync(podcastId: Long): List<Episode> = episodeDao.fetchSync(podcastId)

    override fun fetchSync(podcastId: Long, limit: Int, offset: Int): List<Episode> = episodeDao.fetchSync(podcastId, limit, offset)

    override fun fetch(section: Int): Flow<List<Episode>> = episodeDao.fetch(section)

    override fun fetchSync(section: Int): List<Episode> = episodeDao.fetchSync(section)

    override fun fetchQueue(): Flow<List<Episode>> = episodeDao.fetchQueue()

    override fun fetchQueueSync(): List<Episode> = episodeDao.fetchQueueSync()

    override fun fetch(section: Int, podcastId: Long): Flow<List<Episode>> = episodeDao.fetch(section, podcastId)

    override fun fetchSync(section: Int, podcastId: Long): List<Episode> = episodeDao.fetchSync(section, podcastId)

    override fun fetchEpisodesCountByPodcast(section: Int): Flow<List<PodcastWithCount>> = episodeDao.fetchEpisodesCountByPodcast(section)

    override fun fetchEpisodeCountBySection(podcastId: Long): Flow<SectionWithCount> = episodeDao.fetchEpisodeCountBySection(podcastId)

    override fun getSync(episodeId: Long): Episode? = episodeDao.getSync(episodeId)

    override fun get(episodeId: Long): Flow<Episode> = episodeDao.get(episodeId)

    fun insert(episode: Episode) {
        episode as EpisodeEntity
        episodeDao.insert(episode)
    }

    fun insertIgore(episodes: List<Episode>) {
        episodes.filterIsInstance<EpisodeEntity>()
                .let {
                    episodeDao.insertIgore(it)
                }

        episodes.filterIsInstance<PodcastEpisodeItunes>()
                .map { EpisodeEntity(it.id).apply { updateFromItunes(it) } }
                .let { episodeDao.insertIgore(it) }

    }

    override fun update(episode: Episode) {
        episode as EpisodeEntity
        episodeDao.update(episode)
    }

    fun update(episodes: List<Episode>) {
        episodes.filterIsInstance<EpisodeEntity>()
                .let {
                    episodeDao.update(it)
                }
    }

    fun delete(episode: Episode) {
        episode as EpisodeEntity
        episodeDao.delete(episode)
    }

    fun deleteByPodcast(podcastId: Long) {
        episodeDao.deleteByPodcast(podcastId)
    }

    /**
     * Insert episode if not exists
     */
    fun upsert(episode: Episode) {
        getSync(episode.id).let {
            if (it != null)
                update(episode)
            else
                insert(episode)
        }
    }
}