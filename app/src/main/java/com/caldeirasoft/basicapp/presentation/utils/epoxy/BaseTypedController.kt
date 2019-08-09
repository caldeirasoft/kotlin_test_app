package com.caldeirasoft.basicapp.presentation.utils.epoxy

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import com.caldeirasoft.castly.domain.model.NetworkState

abstract class BaseTypedController<T>: TypedEpoxyController<T>()
{
    var isError: Boolean = false
    open fun toggleLoading(isLoading: Boolean) = Unit
    open fun toggleRetry(retry: Boolean) = Unit

    protected var networkStateData :NetworkState? = null

    var isLoading: Boolean = networkStateData?.isLoading ?: false

    var isInErrorState: Boolean = networkStateData?.isInErrorState ?: false

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

    fun setNetworkState(value: NetworkState?) {
        if (value != networkStateData) {
            networkStateData = value
            setData(currentData)
            //requestDelayedModelBuild(500)
        }
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager is LinearLayoutManager
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        layoutManager.recycleChildrenOnDetach = true
    }
}