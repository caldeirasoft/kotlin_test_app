package com.caldeirasoft.castly.domain.usecase

import androidx.lifecycle.MutableLiveData
import com.caldeirasoft.castly.domain.model.NetworkState
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.repository.ItunesRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.base.UseCaseResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class UpdatePodcastFromItunesUseCase(
        val itunesRepository: ItunesRepository,
        val podcastRepository: PodcastRepository,
        val episodeRepository: EpisodeRepository) {

    fun updatePodcast(podcastId: Long): UseCaseResult<Podcast> {
        val podcastLiveData = podcastRepository.get(podcastId)
        val initialState = MutableLiveData<NetworkState>()
        updateAsync(podcastId, initialState)
        return UseCaseResult(
                data = podcastLiveData,
                initialState = initialState)
    }

    private fun updateAsync(podcastId: Long, initialState: MutableLiveData<NetworkState>) = GlobalScope.async {
        try {
            initialState.postValue(NetworkState.Loading)
            val podcastFromDb = podcastRepository.getSync(podcastId)
            if (podcastFromDb == null) {
                // podcast inexistant en BD : recuperer les données depuis itunes
                // mettre les episodes et le podcast en cache au cas ou l'utilisateur voudrait souscrire
                // renvoyer la liste des episodes paginée
                // et pour chaque pagination récuperer les données depuis le fameux cache
                getPodcastWithEpisodesFromItunes(podcastId)?.let { podcastFromItunes ->
                    podcastRepository.insert(podcastFromItunes)
                    episodeRepository.insertIgore(podcastFromItunes.episodes)
                }
            } else {
                // podcast existant en BD : verifier que le nb d'épisodes en BD correspond au trackcount
                // sinon c'est que l'abonnement au podcast s'est fait sans episodes
                val trackCount: Int = episodeRepository.count(podcastId)
                if ((trackCount == 0) && (podcastFromDb.trackCount > 0)) {
                    getPodcastWithEpisodesFromItunes(podcastId)?.let { podcastFromItunes ->
                        podcastRepository.update(podcastFromItunes)
                        episodeRepository.insertIgore(podcastFromItunes.episodes)
                    }
                } else {
                    // podcast existant en BD : verifier que la date du derniere episode correspond à la date délivrée par itunes
                    // si la date est différente, c'est qu'un nouvel episode est arrivé
                    // si c'est le cas, récuperer le podcast et les episodes depuis itunes, sauvegarder les nouveaux episodes en bd
                    itunesRepository.lookupAsync(podcastId)?.let { podcastFromLookup ->
                        if (podcastFromDb.releaseDate != podcastFromLookup.releaseDate) {
                            getPodcastWithEpisodesFromItunes(podcastId)?.let { podcastFromItunes ->
                                podcastFromDb.releaseDate = podcastFromItunes.releaseDate
                                podcastRepository.update(podcastFromItunes)
                                episodeRepository.insertIgore(podcastFromItunes.episodes)
                            }
                        }
                    }
                }

            }
            initialState.postValue(NetworkState.Success)
        } catch (ex: Exception) {
            initialState.postValue(NetworkState.Error(ex))
        }
    }

    /**
     * Return episodes from podcasts
     */
    private suspend fun getPodcastWithEpisodesFromItunes(podcastId: Long) : Podcast? {
        return itunesRepository.getPodcastAsync("143442-3,26", podcastId)
    }
}