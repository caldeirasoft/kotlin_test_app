package com.caldeirasoft.basicapp.data.repository

import androidx.lifecycle.*
import androidx.paging.*

data class Listing<T> (
    val pagedList: LiveData<PagedList<T>>,
    val networkState: LiveData<NetworkState>,
    val refreshState: LiveData<NetworkState>,
    val refresh: () -> Unit,
    val retry: () -> Unit
)