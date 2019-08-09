package com.caldeirasoft.castly.domain.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.caldeirasoft.castly.domain.const.Constants
import com.caldeirasoft.castly.domain.model.NetworkState

/**
 * Created by Edmond on 15/02/2018.
 */
abstract class BasePagingDataSource<V> : PageKeyedDataSource<Int, V>()
{
    var retry: (() -> Unit)? = null

    val initialState = MutableLiveData<NetworkState>()
    val networkState = MutableLiveData<NetworkState>()

    override fun loadInitial(
            params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, V>
    ) {
        initialState.postValue(NetworkState.Loading)
        networkState.postValue(NetworkState.Loading)
        loadInitialFromSource(params, callback)
    }

    /**
     * Used for [BasePagingDataSource.loadInitial]
     */
    abstract fun loadInitialFromSource(
            params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, V>
    )

    /**
     * Used when [loadInitial] success
     * Make sure that if you have to load before or start, you must complete startedPage correctly
     */
    fun handleLoadInitialSuccess(
            params: LoadInitialParams<Int>,
            callback: LoadInitialCallback<Int, V>,
            data: List<V>?,
            startedPage: Int = Constants.STARTED_PAGE
    ) {
        initialState.postValue(NetworkState.Success)
        networkState.postValue(NetworkState.Success)
        retry = null
        onLoadInitialSuccess(params, callback, data)
        val submitData = data ?: emptyList()
        val previousPage = startedPage - 1
        val nextPage = startedPage + 1
        callback.onResult(submitData, null, nextPage)
    }

    open fun onLoadInitialSuccess(
            params: LoadInitialParams<Int>,
            callback: LoadInitialCallback<Int, V>,
            data: List<V>?
    ) {
    }

    /**
     * Used when [loadInitial] error
     */
    fun handleLoadInitialError(
            params: LoadInitialParams<Int>,
            callback: LoadInitialCallback<Int, V>,
            throwable: Throwable
    ) {
        val error = NetworkState.Error(throwable)
        networkState.postValue(error)
        retry = { loadInitial(params, callback) }
        onLoadInitialError(throwable)
    }

    open fun onLoadInitialError(throwable: Throwable) {
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, V>) {
        networkState.postValue(NetworkState.Loading)
        loadAfterFromSource(params, callback)
    }

    abstract fun loadAfterFromSource(params: LoadParams<Int>, callback: LoadCallback<Int, V>)

    /**
     * Handle when [loadAfterFromSource] success
     */
    fun handleLoadAfterSuccess(
            params: LoadParams<Int>, callback: LoadCallback<Int, V>, data: List<V>?, totalPage: Int?
    ) {
        networkState.postValue(NetworkState.Success)
        retry = null
        onLoadAfterSuccess(params, callback, data)
        val submitData = data ?: emptyList()
        val nextKey = if (params.key == totalPage) null else (params.key + 1)
        callback.onResult(submitData, nextKey)
    }

    open fun onLoadAfterSuccess(
            params: LoadParams<Int>,
            callback: LoadCallback<Int, V>,
            data: List<V>?
    ) {
    }

    /**
     * Handle when [loadAfter] success
     */
    fun handleLoadAfterError(
            params: LoadParams<Int>, callback: LoadCallback<Int, V>, throwable: Throwable
    ) {
        val error = NetworkState.Error(throwable)
        networkState.postValue(error)
        retry = { loadAfter(params, callback) }
        onLoadAfterError(throwable)
    }

    open fun onLoadAfterError(throwable: Throwable) {
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, V>) { }

}