package com.caldeirasoft.basicapp.ui.adapter


/**
 * Created by sisel on 2018/3/12.
 */
interface ItemViewClickListener<in T> {
    fun onItemClick(item: T?, position: Int, viewId: Int)
}