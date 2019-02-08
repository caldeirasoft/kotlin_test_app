package com.caldeirasoft.basicapp.presentation.utils.epoxy

import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import com.airbnb.epoxy.EpoxyModel
import kotlin.math.roundToInt


abstract class SwipeAwayCallbacks<T : EpoxyModel<*>>(
        private val icon: Drawable,
        private val padding: Int,
        @ColorInt private val backgroundColor: Int,
        @ColorInt private val accentColor: Int
) : SwipeCallbacks<T>(icon, padding, backgroundColor, accentColor) {

}