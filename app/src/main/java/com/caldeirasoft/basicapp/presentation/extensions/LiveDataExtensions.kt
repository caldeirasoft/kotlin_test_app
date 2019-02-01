package com.caldeirasoft.basicapp.presentation.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

inline fun <T> LiveData<T>.observeK(owner: LifecycleOwner, crossinline observer: (T?) -> Unit) {
    this.observe(owner, Observer { observer(it) })
}
