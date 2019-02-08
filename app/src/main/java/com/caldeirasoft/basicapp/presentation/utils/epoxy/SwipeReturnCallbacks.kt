package com.caldeirasoft.basicapp.presentation.utils.epoxy

import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import com.airbnb.epoxy.EpoxyModel
import kotlin.math.roundToInt


abstract class SwipeReturnCallbacks<T : EpoxyModel<*>>(
        private val icon: Drawable,
        private val padding: Int,
        @ColorInt private val backgroundColor: Int,
        @ColorInt private val accentColor: Int
) : SwipeCallbacks<T>(icon, padding, backgroundColor, accentColor) {

    private var mAdapterPosition:Int? = null
    private var mSwipeProgress:Float = 0f;

    companion object {
        const val COMPLETION_THRESHOLD = 0.5f
        const val MAX_SWIPE_ESCAPE_VELOCITY = 1601f
    }

    override fun onSwipeStarted(model: T, itemView: View, adapterPosition: Int) {
        super.onSwipeStarted(model, itemView, adapterPosition)
        mAdapterPosition = adapterPosition
    }

    override fun onSwipeProgressChanged(model: T, itemView: View, swipeProgress: Float, canvas: Canvas) {
        super.onSwipeProgressChanged(model, itemView, swipeProgress, canvas)
        mSwipeProgress = swipeProgress
    }

    override fun onSwipeReleased(model: T, itemView: View) {
        if (mSwipeProgress > getCompletionThreshold()) {
            mAdapterPosition?.let {
                onSwipeCompleted(model, itemView, it, 0)
            }
        }

        super.onSwipeReleased(model, itemView)
        mSwipeProgress = 0f
        mAdapterPosition = null
    }

    override fun getCompletionThreshold(): Float = COMPLETION_THRESHOLD

    override fun getSwipeThreshold(): Float = 1f

    override fun getSwipeEscapeVelocity(): Float = MAX_SWIPE_ESCAPE_VELOCITY
}