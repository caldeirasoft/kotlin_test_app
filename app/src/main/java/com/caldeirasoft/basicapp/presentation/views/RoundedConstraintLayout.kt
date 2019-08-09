package com.caldeirasoft.basicapp.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.BindingAdapter
import com.caldeirasoft.basicapp.R
import com.google.android.material.appbar.AppBarLayout
import kotlin.properties.Delegates

/**
 * Custom [MotionLayout] implementation to animate the app bar.
 *
 * - The progress of the transition executed by a [MotionLayout] can be controlled using
 *   [MotionLayout.setProgress].
 * - Changes in the size of the [AppBarLayout] can be detected with a listener
 *   ([AppBarLayout.OnOffsetChangedListener]) and obtain the delta change (as a vertical offset).
 *
 * This custom view uses both capabilities to detect the vertical offset of the [AppBarLayout] change
 * and update the progress of the transition accordingly. That way, we have a custom [MotionLayout]
 * that can change the content with a transition that is based in the interaction of the user
 * with a scrollable view in a [CoordinatorLayout].
 *
 * Source: https://medium.com/google-developers/introduction-to-motionlayout-part-iii-47cd64d51a5
 */
class RoundedConstraintLayout : ConstraintLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 2)
        if (roundedTopCorners)
            View.mergeDrawableStates(drawableState, ROUNDED_TOP_CORNERS)
        if (roundedBottomCorners)
            View.mergeDrawableStates(drawableState, ROUNDED_BOTTOM_CORNERS)
        return drawableState
    }

    var roundedTopCorners: Boolean = false
        get
        set(value) {
            if (value != field) {
                field = value
                refreshDrawableState()
            }
        }

    var roundedBottomCorners: Boolean = false
        get
        set(value) {
            if (value != field) {
                field = value
                refreshDrawableState()
            }
        }

    companion object {
        private val ROUNDED_TOP_CORNERS = intArrayOf(R.attr.roundedTopCorners)
        private val ROUNDED_BOTTOM_CORNERS = intArrayOf(R.attr.roundedBottomCorners)

        @BindingAdapter("roundedTopCorners")
        @JvmStatic
        fun setRoundedTopCorners(view: RoundedConstraintLayout, value: Boolean?) {
            value?.let { view.roundedTopCorners = it }
        }

        @BindingAdapter("roundedBottomCorners")
        @JvmStatic
        fun setRoundedBottomCorners(view: RoundedConstraintLayout, value: Boolean?) {
            value?.let { view.roundedBottomCorners = it }
        }
    }
}