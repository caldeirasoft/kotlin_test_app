package com.caldeirasoft.castly.domain.usecase.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.caldeirasoft.castly.domain.model.entities.NetworkState
import kotlinx.coroutines.flow.Flow

data class UseCaseResult<T>(
        val data: Flow<T>,
        val retry: (() -> Unit)? = null
)