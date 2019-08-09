package com.caldeirasoft.basicapp.presentation.utils.epoxy

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyAsyncUtil
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.caldeirasoft.basicapp.presentation.utils.defaultItemDiffCallback

abstract class BasePagedController<T>(
        itemDiffCallback: DiffUtil.ItemCallback<T> = defaultItemDiffCallback())
    : PagedListEpoxyController<T>(
                diffingHandler = EpoxyAsyncUtil.getAsyncBackgroundHandler(),
                itemDiffCallback = itemDiffCallback
        )
{
    var isError: Boolean = false
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

    var error: String? = ""
        set(value) {
            field = value?.let {
                isError = true
                it
            } ?: run {
                isError = false
                null
            }
            if (isError) {
                requestModelBuild()
            }
        }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager is LinearLayoutManager
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        layoutManager.recycleChildrenOnDetach = true
    }
}