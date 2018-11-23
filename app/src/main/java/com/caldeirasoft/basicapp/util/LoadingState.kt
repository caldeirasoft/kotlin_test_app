package com.caldeirasoft.basicapp.util

import androidx.annotation.IntegerRes

@Suppress("DataClassPrivateConstructor")
enum class LoadingState constructor(
        val state: Int,
        val msg: String = "",
        @IntegerRes val msgRes: Int = 0
    )
{
    OK(0),
    EMPTY(1),
    LOADING(2),
    LOAD_ERR(3),
    LOADING_MORE(4),
    LOAD_MORE_ERR(5),
    LOAD_MORE_COMPLETE(6);

    val isOK get() = state == OK.state
    val isEmpty get() = state == EMPTY.state
    val isLoading get() = state == LOADING.state
    val isLoadErr get() = state == LOAD_ERR.state
    val isLoadingMore get() = state == LOADING_MORE.state
    val isLoadMoreErr get() = state == LOAD_MORE_ERR.state
    val isLoadMoreComplete get() = state == LOAD_MORE_COMPLETE.state
    val isShowContent get() = !isEmpty && !isLoadErr && !isLoading
    val isShowMore get() = isLoadingMore || isLoadMoreComplete || isLoadMoreErr

    /*
    val message
        get() = {
            if (msg.isNotEmpty()) {
                msg
            } else if (msgRes != 0) {
                app.getString(msgRes)
            } else if (isEmpty) {
                app.getString(R.string.liststate_empty)
            } else if (isLoadErr || isLoadMoreErr) {
                app.getString(R.string.liststate_error)
            } else if (isLoadingMore) {
                app.getString(R.string.liststate_loading_more)
            } else if (isLoadMoreComplete) {
                app.getString(R.string.liststate_no_more)
            } else if (isLoading) {
                app.getString(R.string.liststate_loading)
            } else {
                msg
            }
        }
        */

    fun checkLoading(func: () -> Unit) {
        if (isLoading) {
            return
        }
        func()
    }

    fun checkLoadingMore(func: () -> Unit) {
        if (isLoadingMore) {
            return
        }
        func()
    }

    fun checkSame(another: LoadingState?, func: () -> Unit) {
        if (another === this) {
            func()
        }
    }
}