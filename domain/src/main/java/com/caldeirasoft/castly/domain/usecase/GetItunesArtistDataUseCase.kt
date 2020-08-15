package com.caldeirasoft.castly.domain.usecase

import androidx.lifecycle.MutableLiveData
import com.caldeirasoft.castly.domain.model.entities.NetworkState
import com.caldeirasoft.castly.domain.model.itunes.ArtistPageData
import com.caldeirasoft.castly.domain.model.itunes.PodcastCollection
import com.caldeirasoft.castly.domain.repository.ItunesRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.base.UseCaseResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class GetItunesArtistDataUseCase(val podcastRepository: PodcastRepository,
                                 val itunesRepository: ItunesRepository) {

    fun execute(storeFront: String, artistId: Long): UseCaseResult<ArtistPageData> {
        val initialState = MutableLiveData<NetworkState>()
        val storeLiveData = MutableLiveData<ArtistPageData>()
        GlobalScope.launch {
            getStoreDataAsync(storeFront, artistId, initialState, storeLiveData)
        }
        return UseCaseResult(
                data = flowOf(storeLiveData.value!!))
    }

    private suspend fun getStoreDataAsync(
            storeFront: String,
            artistId: Long,
            initialState: MutableLiveData<NetworkState>,
            result: MutableLiveData<ArtistPageData>) {
        try {
            initialState.postValue(NetworkState.Loading)
            val storeData = itunesRepository.getArtistPageDataAsync(storeFront, artistId)
            result.postValue(storeData)
            initialState.postValue(NetworkState.Success)

            storeData.forEach { collection ->
                // get remaining lookups
                val lockupKeys = storeData.mapPodcastLookup.keys
                if (!lockupKeys.containsAll(collection.ids)) {
                    val missingKeys = collection.ids.subtract(lockupKeys)
                    val missingPodcasts = itunesRepository.lookupAsync(missingKeys.toList())
                    missingPodcasts.forEach {
                        storeData.mapPodcastLookup.put(it.id, it)
                    }
                }

                // add all podcasts
                collection.ids
                        .mapNotNull { storeData.mapPodcastLookup[it] }
                        .let { list ->
                            collection.items.addAll(list)
                        }
                result.postValue(storeData)
            }
        } catch (ex: Exception) {
            initialState.postValue(NetworkState.Error(ex))
        }
    }
}