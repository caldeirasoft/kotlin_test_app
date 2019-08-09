package com.caldeirasoft.basicapp.presentation.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

abstract class ListItemViewModel<T>(val mediaId: String)
    : ViewModel()
{
    // data items
    abstract val dataItems: LiveData<List<T>>

    fun refresh() {
    }
}