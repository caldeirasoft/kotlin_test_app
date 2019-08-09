package com.caldeirasoft.castly.domain.usecase.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.caldeirasoft.castly.domain.model.NetworkState

data class UseCaseResult<T>(
        val data: LiveData<T>,
        val networkState: LiveData<NetworkState> = MutableLiveData(),
        val initialState: LiveData<NetworkState> = MutableLiveData(),
        val retry: (() -> Unit)? = null
)