package com.caldeirasoft.castly.domain.usecase

import androidx.lifecycle.MutableLiveData
import com.caldeirasoft.castly.domain.model.NetworkState
import com.caldeirasoft.castly.domain.model.itunes.StoreData
import com.caldeirasoft.castly.domain.repository.ItunesRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.base.UseCaseResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GetItunesStoreUseCase(val podcastRepository: PodcastRepository,
                            val itunesRepository: ItunesRepository) {

    fun execute(storeFront: String, genreId: Int): UseCaseResult<StoreData> {
        val initialState = MutableLiveData<NetworkState>()
        val storeLiveData = MutableLiveData<StoreData>()
        GlobalScope.launch {
            getStoreDataAsync(storeFront, genreId, initialState, storeLiveData)
        }
        return UseCaseResult(
                data = storeLiveData,
                initialState = initialState)
    }

    private suspend fun getStoreDataAsync(storeFront: String, genreId: Int, initialState: MutableLiveData<NetworkState>, result: MutableLiveData<StoreData>)
    {
        try {
            initialState.postValue(NetworkState.Loading)
            var storeData = StoreData()
            //var storeData = itunesRepository.getGenreDataAsync(storeFront, genreId)
            result.postValue(storeData)
            initialState.postValue(NetworkState.Success)
        } catch (ex: Exception) {
            initialState.postValue(NetworkState.Error(ex))
        }
    }
}