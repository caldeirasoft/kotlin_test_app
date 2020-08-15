package com.caldeirasoft.castly.domain.util

import androidx.paging.PagedList
import com.caldeirasoft.castly.domain.datasource.PaginationResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

/**
 * A generic class that holds a value with its loading status.
 *
 */
@FlowPreview
@ExperimentalCoroutinesApi
interface FlowPaginationModel<T> {
    val pagedList: Flow<PagedList<T>>
    val getState: Flow<PaginationResource>
    fun refresh()
}