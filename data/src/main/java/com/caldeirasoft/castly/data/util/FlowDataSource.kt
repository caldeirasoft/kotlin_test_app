package com.caldeirasoft.castly.data.util

import com.caldeirasoft.castly.domain.datasource.PaginationResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

/**
 * Copyright 2020, Kurt Renzo Acosta, All rights reserved.
 *
 * @author Kurt Renzo Acosta
 * @since 01/10/2020
 */

@FlowPreview
@ExperimentalCoroutinesApi
interface FlowDataSource<T> {
    val getState: Flow<PaginationResource>
    val totalCount: Flow<Int>
    fun refresh()
}