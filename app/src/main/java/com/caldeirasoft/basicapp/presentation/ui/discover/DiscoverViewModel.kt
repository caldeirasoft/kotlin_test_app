package com.caldeirasoft.basicapp.presentation.ui.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caldeirasoft.basicapp.domain.entity.ItunesStore
import com.caldeirasoft.basicapp.domain.repository.ItunesRepository
import com.caldeirasoft.basicapp.domain.repository.PodcastRepository
import com.caldeirasoft.basicapp.domain.usecase.GetItunesStoreUseCase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DiscoverViewModel(val getItunesStoreUseCase: GetItunesStoreUseCase) : ViewModel() {

    private var isDiscoverLoaded = false
    private val _isLoading = MutableLiveData<Boolean>()

    val isLoading = _isLoading as LiveData<Boolean>
    var itunesStore = MutableLiveData<ItunesStore>()

    fun request() {
        GlobalScope.launch {
            if (!isDiscoverLoaded) {
                isDiscoverLoaded = true

                getItunesStoreUseCase.beforeExecute = { _isLoading.postValue(true) }
                getItunesStoreUseCase.terminated = { _isLoading.postValue(false) }
                getItunesStoreUseCase.failed = { it.printStackTrace() }

                val r = getItunesStoreUseCase
                        .execute("143442-3,31")
                        .await()
                        .data
                        ?.let {
                            itunesStore.postValue(it)
                        }
            }
        }
    }
}