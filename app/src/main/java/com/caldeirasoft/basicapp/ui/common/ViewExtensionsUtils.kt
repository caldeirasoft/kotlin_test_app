package com.caldeirasoft.basicapp.ui.common

import android.view.View

fun View.visibleGone(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.visibleInvisible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}