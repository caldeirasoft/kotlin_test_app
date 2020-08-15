package com.caldeirasoft.castly.data.util

import com.caldeirasoft.castly.data.datasources.remote.ITunesApi
import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.domain.datasource.PaginationResource
import com.caldeirasoft.castly.domain.repository.ItunesRepository
import androidx.paging.PageKeyedDataSource as AndroidXPageKeyedDataSource
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import retrofit2.HttpException
import retrofit2.Response
import kotlin.math.min
import androidx.paging.DataSource as AndroidXDataSource

/**
 * Copyright 2020, Kurt Renzo Acosta, All rights reserved.
 *
 * @author Kurt Renzo Acosta
 * @since 01/10/2020
 */

@ExperimentalCoroutinesApi
@FlowPreview
class ItunesTopPodcastsDataSource constructor(
        private val clientScope: CoroutineScope,
        private val itunesRepository: ItunesRepository,
        private val genreId: Int
) : AndroidXPageKeyedDataSource<Int, Podcast>(), FlowDataSource<Podcast> {
    private var ids: List<Long> = emptyList()

    private val _getState = ConflatedBroadcastChannel<PaginationResource>()
    override val getState = _getState.asFlow()

    private val _totalCount = ConflatedBroadcastChannel(0)
    override val totalCount = _totalCount.asFlow()

    suspend fun initPodcastsIds() {
        ids = itunesRepository.topAsync(genreId)
    }

    suspend fun getPodcasts(key: Int, requestLoadSize: Int): List<Podcast> {
        val podcastsList: ArrayList<Podcast> = arrayListOf()
        val startPosition = key * requestLoadSize
        if (startPosition > ids.size - 1) {
            return podcastsList
        }

        val idsSubset = ids.subList(startPosition, min(ids.size - 1, startPosition + requestLoadSize))
        // request Podcasts from Ids
        if (idsSubset.isNotEmpty()) {
            podcastsList.addAll(itunesRepository.lookupAsync(idsSubset))
        }
        return podcastsList
    }

    override fun loadInitial(
            params: LoadInitialParams<Int>,
            callback: LoadInitialCallback<Int, Podcast>
    ) {
        clientScope.launch(CoroutineExceptionHandler { _, exception ->
            _getState.offer(PaginationResource.error(exception.localizedMessage))
        }) {
            _getState.offer(PaginationResource.loading())
            initPodcastsIds()
            val items = getPodcasts(1, params.requestedLoadSize)
            val count = 300
            _totalCount.offer(count)
            callback.onResult(items, 0, count, null, 4)
            _getState.offer(PaginationResource.success())
            if (items.isEmpty()) {
                _getState.offer(PaginationResource.empty())
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Podcast>) {
        clientScope.launch(CoroutineExceptionHandler { _, exception ->
            _getState.offer(PaginationResource.error(exception.localizedMessage))
        }) {
            val items = getPodcasts(params.key, params.requestedLoadSize)
            callback.onResult(items, params.key + 1)
            _getState.offer(PaginationResource.success())
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Podcast>) {
        clientScope.launch(CoroutineExceptionHandler { _, exception ->
            _getState.offer(PaginationResource.error(exception.localizedMessage))
        }) {
            val items = getPodcasts(params.key, params.requestedLoadSize)
            callback.onResult(items, params.key - 1)
            _getState.offer(PaginationResource.success())
        }
    }

    override fun refresh() {
        invalidate()
    }

    private suspend fun <ResponseType> processResponse(apiResponse: Response<ResponseType>): ResponseType {
        when {
            apiResponse.isSuccessful -> {
                val body = apiResponse.body() ?: throw HttpException(apiResponse)
                return body
            }
            else -> {
                throw HttpException(apiResponse)
            }
        }
    }

    internal class Factory constructor(
            private val clientScope: CoroutineScope,
            private val itunesRepository: ItunesRepository,
            private val genreId: Int
    ) : AndroidXDataSource.Factory<Int, Podcast>() {

        private val _dataSource = ConflatedBroadcastChannel<ItunesTopPodcastsDataSource>()
        val dataSource = _dataSource.asFlow()

        override fun create(): AndroidXPageKeyedDataSource<Int, Podcast> {
            val source = ItunesTopPodcastsDataSource(
                    clientScope,
                    itunesRepository,
                    genreId
            )
            _dataSource.offer(source)
            return source
        }
    }
}