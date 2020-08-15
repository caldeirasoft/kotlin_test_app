package com.caldeirasoft.castly.data.util

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.caldeirasoft.castly.domain.util.Resource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import retrofit2.Response

/**
 * A generic class that can provide a resource backed by both
 * the SQLite database and the network.
 *
 * [ResultType] represents the type for database.
 * [RequestType] represents the type for network.
 */
@FlowPreview
@kotlinx.coroutines.ExperimentalCoroutinesApi
abstract class NetworkBoundDbResource<ResponseType, EntityType>(
        private val defaultValue: EntityType? = null) {
    fun asFlow(): Flow<Resource<EntityType>> = flow {
        val flow = loadFromDb()
                .onStart { emit(Resource.loading(defaultValue)) }
                .flatMapConcat { data ->
                    if (shouldFetch(data)) {
                        emit(Resource.loading(data))
                        try {
                            val apiResponse = fetchFromNetwork()
                            when {
                                apiResponse.isSuccessful && apiResponse.body() != null -> {
                                    apiResponse.body()?.let {
                                        saveCallResult(it)
                                    }
                                    loadFromDb().map { Resource.success(it) }
                                }
                                else -> {
                                    throw HttpException(apiResponse)
                                }
                            }
                        } catch (throwable: Throwable) {
                            onFetchFailed(throwable)
                            loadFromDb().map { Resource.error(throwable.message ?: "", it) }
                        }
                    } else {
                        loadFromDb().map { Resource.success(it) }
                    }
                }
        emitAll(flow)
    }

    @WorkerThread
    protected abstract suspend fun saveCallResult(response: ResponseType)

    @MainThread
    protected abstract fun shouldFetch(data: EntityType?): Boolean

    @MainThread
    open fun onFetchFailed(throwable: Throwable): Unit = Unit

    @MainThread
    protected abstract fun loadFromDb(): Flow<EntityType>

    @MainThread
    protected abstract suspend fun fetchFromNetwork(): Response<ResponseType>
}