package com.caldeirasoft.basicapp.ui.epoxy

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyAsyncUtil
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.podcastItem
import com.caldeirasoft.basicapp.ui.adapter.defaultItemDiffCallback

abstract class BasePagedController<T>(val itemDiffCallback: DiffUtil.ItemCallback<T> = defaultItemDiffCallback()) :
        PagedListEpoxyController<T>(
                modelBuildingHandler = EpoxyAsyncUtil.getAsyncBackgroundHandler(),
                diffingHandler = EpoxyAsyncUtil.getAsyncBackgroundHandler(),
                itemDiffCallback = itemDiffCallback
        )
{
    open fun toggleLoading(isLoading: Boolean) = Unit
    open fun toggleRetry(retry: Boolean) = Unit

    var isLoading = false
        set(value) {
            if (value != field) {
                field = value
                requestDelayedModelBuild(500)
            }
        }

    var retry = false
        set(value) {
            if (value != field) {
                field = value
                requestDelayedModelBuild(200)
            }
        }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager is LinearLayoutManager
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        layoutManager.recycleChildrenOnDetach = true
    }
}