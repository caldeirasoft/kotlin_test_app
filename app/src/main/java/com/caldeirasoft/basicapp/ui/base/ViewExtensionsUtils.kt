package com.caldeirasoft.basicapp.ui.base

import android.view.View

fun View.changeVisibility(visible: Boolean) {
    if (visible) {
        this.visibility = View.VISIBLE
    }
    else {
        this.visibility = View.GONE
    }
}