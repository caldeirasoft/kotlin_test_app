package com.caldeirasoft.castly.data.util

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.caldeirasoft.castly.domain.util.Resource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
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
abstract class NetworkBoundFileResource<ResponseType, RequestedType>() {
    private val _getData = ConflatedBroadcastChannel<Resource<RequestedType>>()
    val getData = _getData.asFlow()

    fun asFlow(): Flow<Resource<RequestedType>> = flow {
        // send loading state
        emit(Resource.loading(null))

        // offer file
        val persistedData = loadFromFile()
        if (shouldFetch(persistedData)) {
            emit(Resource.loading(persistedData))
            try {
                val apiResponse = fetchFromNetwork()
                when {
                    apiResponse.isSuccessful -> {
                        val body = apiResponse.body()
                                ?: throw HttpException(apiResponse)
                        processResponse(body).let {
                            emit(Resource.success(it))
                            saveCallResult(it)
                        }
                    }
                    else -> {
                        throw HttpException(apiResponse)
                    }
                }

            } catch (throwable: Throwable) {
                onFetchFailed(throwable)
                emit(Resource.error(throwable.message ?: "", persistedData))
            }
        } else {
            emit(Resource.success(persistedData))
        }
    }


    @WorkerThread
    protected abstract suspend fun processResponse(response: ResponseType): RequestedType

    @WorkerThread
    protected abstract suspend fun saveCallResult(data: RequestedType)

    @MainThread
    protected abstract fun shouldFetch(data: RequestedType?): Boolean

    @MainThread
    open fun onFetchFailed(throwable: Throwable): Unit = Unit

    @MainThread
    protected abstract fun loadFromFile(): RequestedType?

    @MainThread
    protected abstract suspend fun fetchFromNetwork(): Response<ResponseType>
}