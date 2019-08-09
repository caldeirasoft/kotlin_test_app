package com.caldeirasoft.castly.domain.usecase

import androidx.lifecycle.MutableLiveData
import com.caldeirasoft.castly.domain.model.NetworkState
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.base.UseCaseResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class SubscribeToPodcastUseCase(val podcastRepository: PodcastRepository) {

    fun subscribe(podcastId: Long): UseCaseResult<Boolean> {
        val initialState = MutableLiveData<NetworkState>()
        subscribeAsync(podcastId, initialState)
        return UseCaseResult(
                data = MutableLiveData(),
                initialState = initialState)
    }

    private fun subscribeAsync(podcastId: Long, initialState: MutableLiveData<NetworkState>) = GlobalScope.async {
        try {
            initialState.postValue(NetworkState.Loading)
            podcastRepository.getSync(podcastId)?.let {podcast ->
                podcast.isSubscribed = true
                podcastRepository.update(podcast)
            }
            initialState.postValue(NetworkState.Success)
        } catch (ex: Exception) {
            initialState.postValue(NetworkState.Error(ex))
        }
    }
}