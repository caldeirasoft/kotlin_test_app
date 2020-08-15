package com.caldeirasoft.castly.domain.usecase

import androidx.lifecycle.MutableLiveData
import com.caldeirasoft.castly.domain.model.entities.NetworkState
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.base.UseCaseResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flowOf

class UnsubscribeFromPodcastUseCase(val podcastRepository: PodcastRepository) {

    fun unsubscribe(podcastId: Long): UseCaseResult<Boolean> {
        val initialState = MutableLiveData<NetworkState>()
        unsubscribeAsync(podcastId, initialState)
        return UseCaseResult(
                data = flowOf())
    }

    private fun unsubscribeAsync(podcastId: Long, initialState: MutableLiveData<NetworkState>) = GlobalScope.async {
        try {
            initialState.postValue(NetworkState.Loading)
            /*podcastRepository.getSync(podcastId)?.let {podcast ->
                podcast.isSubscribed = false
                podcastRepository.update(podcast)
            }*/
            initialState.postValue(NetworkState.Success)
        } catch (ex: Exception) {
            initialState.postValue(NetworkState.Error(ex))
        }
    }
}