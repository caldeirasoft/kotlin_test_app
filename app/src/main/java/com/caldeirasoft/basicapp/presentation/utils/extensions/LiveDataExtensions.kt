package com.caldeirasoft.basicapp.presentation.utils.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

inline fun <T> LiveData<T>.observeK(owner: LifecycleOwner, crossinline observer: (T?) -> Unit) {
    this.observe(owner, Observer { observer(it) })
}

inline fun <T, K, R> LiveData<T>.combineWith(
        liveData: LiveData<K>,
        crossinline block: (T?, K?) -> R
): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this) {
        result.value = block.invoke(this.value, liveData.value)
    }
    result.addSource(liveData) {
        result.value = block.invoke(this.value, liveData.value)
    }
    return result
}

inline fun <T1, T2, R> combineLatest(
        source1: LiveData<T1>,
        source2: LiveData<T2>,
        crossinline combiner: (T1?, T2?) -> R?): LiveData<R> {
    val mediator = MediatorLiveData<R>()
    val combinerFunction = {
        val source1Value = source1.value
        val source2Value = source2.value
        mediator.value = combiner.invoke(source1Value, source2Value)
    }

    mediator.addSource(source1) { combinerFunction() }
    mediator.addSource(source2) { combinerFunction() }
    return mediator
}

inline fun <T1, T2, T3, R> combineLatest(
        source1: LiveData<T1>,
        source2: LiveData<T2>,
        source3: LiveData<T3>,
        crossinline combiner: (T1?, T2?, T3?) -> R?): LiveData<R> {
    val mediator = MediatorLiveData<R>()
    val combinerFunction = {
        val source1Value = source1.value
        val source2Value = source2.value
        val source3Value = source3.value
        mediator.value = combiner.invoke(source1Value, source2Value, source3Value)
    }

    mediator.addSource(source1) { combinerFunction() }
    mediator.addSource(source2) { combinerFunction() }
    mediator.addSource(source3) { combinerFunction() }
    return mediator
}