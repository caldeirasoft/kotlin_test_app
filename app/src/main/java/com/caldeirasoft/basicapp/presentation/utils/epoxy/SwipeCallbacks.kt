package com.caldeirasoft.basicapp.presentation.utils.epoxy

import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import com.airbnb.epoxy.EpoxyModel
import kotlin.math.roundToInt


abstract class SwipeCallbacks<T : EpoxyModel<*>>(
        private val icon: Drawable,
        private val padding: Int,
        @ColorInt private val backgroundColor: Int,
        @ColorInt private val accentColor: Int
) : EpoxyTouchHelperExt.SwipeCallbacksExt<T>() {

    private val rect = RectF()
    private val iconBounds = Rect()

    private val iconHeight = icon.intrinsicHeight
    private val iconWidth = icon.intrinsicWidth

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = backgroundColor
    }

    private val foregroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = accentColor
    }

    companion object {
        const val MAX_ICON_SCALE = 1.3f
        const val MIN_ICON_SCALE = 0.8f
    }


    override fun onSwipeProgressChanged(
            model: T,
            itemView: View,
            swipeProgress: Float,
            canvas: Canvas
    ) {
        rect.set(
                itemView.left.toFloat(),
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
        )
        rect.offset(itemView.translationX, itemView.translationY)

        var radius = 0f

        val save = canvas.save()

        if (rect.left > 0) {
            // Swiping left-to-right
            rect.right = rect.left
            rect.left = 0f

            canvas.clipRect(rect)

            val startValue = iconBounds.right + padding / 2
            val endValue = rect.left + itemView.width * getCompletionThreshold()
            if (rect.right >= startValue) {
                val fraction =
                        ((rect.right - startValue) / (endValue - startValue)).coerceIn(0f, 1f)
                val maxRadius = Math.hypot(
                        rect.right.toDouble() - iconBounds.centerX(),
                        iconBounds.centerY().toDouble()
                )
                radius = lerp(0f, maxRadius.toFloat(), fraction)
            }

            val left = rect.left.roundToInt() + padding
            val top = (rect.top + (rect.height() - iconHeight) / 2).roundToInt()
            iconBounds.set(left, top, left + iconWidth, top + iconHeight)
            icon.bounds = iconBounds
        } else if (rect.right < canvas.width) {
            // Swiping right-to-left
            rect.left = rect.right
            rect.right = canvas.width.toFloat()

            canvas.clipRect(rect)

            val startValue = iconBounds.left - padding / 2
            val endValue = rect.right - itemView.width * getCompletionThreshold()
            if (rect.left <= startValue) {
                val fraction = ((rect.left - startValue) / (endValue - startValue)).coerceIn(0f, 1f)
                val maxRadius = Math.hypot(
                        iconBounds.centerX() - rect.left.toDouble(),
                        iconBounds.centerY().toDouble()
                )
                radius = lerp(0f, maxRadius.toFloat(), fraction)
            }

            val right = rect.right.roundToInt() - padding
            val top = (rect.top + (rect.height() - iconHeight) / 2).roundToInt()
            iconBounds.set(right - iconWidth, top, right, top + iconHeight)
            icon.bounds = iconBounds
        }

        // Draw the background color
        canvas.drawRect(rect, backgroundPaint)

        if (radius > 0) {
            canvas.drawCircle(
                    iconBounds.centerX().toFloat(),
                    iconBounds.centerY().toFloat(),
                    radius,
                    foregroundPaint
            )
        }

        // Now draw the icon
        icon.draw(canvas)

        canvas.restoreToCount(save)
    }

    open fun getCompletionThreshold(): Float {
        return super.getSwipeThreshold()
    }

    override fun getSwipeThreshold(): Float {
        return super.getSwipeThreshold()
    }

    fun lerp(startValue: Float, endValue: Float, fraction: Float) =
            startValue + fraction * (endValue - startValue)
}