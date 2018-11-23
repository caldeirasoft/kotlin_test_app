package com.caldeirasoft.basicapp.ui.adapter


/**
 * Created by sisel on 2018/3/12.
 */
interface ItemViewSelectedListener<in T> {
    fun onItemSelected(item: T?, position: Int)
}