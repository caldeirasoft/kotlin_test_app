package com.caldeirasoft.castly.data.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource

//////////////////////////////////////// FLOWABLE EXTENSIONS ///////////////////////////////////////

fun <IN : OUT, OUT : Any?> LiveData<IN>.convert(): LiveData<OUT> {
    return Transformations.map(this) { item -> item as OUT ?: null }
}

fun <IN : OUT, OUT : Any?> LiveData<List<IN>>.convertAll(): LiveData<List<OUT>> {
    return Transformations.map(this) { list -> list.map { it as OUT }}
}


fun <IN : OUT, OUT : Any?> DataSource.Factory<Int, IN>.convertAll(): DataSource.Factory<Int, OUT> {
    return this.map { it as OUT}
}