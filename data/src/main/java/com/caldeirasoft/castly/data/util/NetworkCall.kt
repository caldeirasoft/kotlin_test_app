package com.caldeirasoft.castly.data.util

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.caldeirasoft.castly.domain.util.Resource
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import retrofit2.Response

abstract class NetworkCall<ResponseType, EntityType> {

    fun asFlow(): Flow<Resource<EntityType>> = flow {
        emit(Resource.loading(null))

        try {
            val apiResponse = fetchFromNetwork()
            when {
                apiResponse.isSuccessful && apiResponse.body() != null -> {
                    apiResponse.body()?.let { response ->
                        convertNetworkResult(response).let { entity ->
                            emit(Resource.success(entity))
                        }
                    } ?: emit(Resource.success(null))
                }
                else -> {
                    throw HttpException(apiResponse)
                }
            }
        } catch (throwable: Throwable) {
            onFetchFailed(throwable)
            emit(Resource.error(throwable.message ?: "", null))
        }
    }

    @WorkerThread
    protected abstract suspend fun convertNetworkResult(response: ResponseType): EntityType

    @MainThread
    open fun onFetchFailed(throwable: Throwable): Unit = Unit

    @MainThread
    protected abstract suspend fun fetchFromNetwork(): Response<ResponseType>
}