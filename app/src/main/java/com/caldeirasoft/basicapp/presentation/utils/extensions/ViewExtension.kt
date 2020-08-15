package com.caldeirasoft.basicapp.presentation.utils.extensions

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat

fun View.setHeight(@Px heightPx: Int) {
    val params = this.layoutParams
    when (params) {
        is FrameLayout.LayoutParams -> params.height = heightPx
        is LinearLayout.LayoutParams -> params.height = heightPx
        is RelativeLayout.LayoutParams -> params.height = heightPx
        is CoordinatorLayout.LayoutParams -> params.height = heightPx
        is ConstraintLayout.LayoutParams -> params.height = heightPx
    }
    layoutParams = params
}

fun View.setWidth(@Px heightPx: Int) {
    val params = this.layoutParams
    when (params) {
        is FrameLayout.LayoutParams -> params.width = heightPx
        is LinearLayout.LayoutParams -> params.width = heightPx
        is RelativeLayout.LayoutParams -> params.width = heightPx
        is CoordinatorLayout.LayoutParams -> params.width = heightPx
        is ConstraintLayout.LayoutParams -> params.width = heightPx
    }
    layoutParams = params
}