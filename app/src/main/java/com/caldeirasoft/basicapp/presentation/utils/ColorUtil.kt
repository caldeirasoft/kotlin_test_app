package com.caldeirasoft.basicapp.presentation.utils

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import kotlin.math.roundToInt

object ColorUtil {
    const val COLOR_PREFIX_LITERAL = "#"

    fun isColor(color: String): Boolean = color.startsWith(COLOR_PREFIX_LITERAL)
    
    fun modulateColorAlpha(baseColor: Int, alphaMod: Float): Int =
            when (alphaMod) {
                1.0f -> baseColor
                else -> baseColor and 0xFFFFFF or (constrain(((baseColor shr 24) * alphaMod + 0.5f).toInt(), 0, 255) shl 24)
            }

    private fun constrain(amount: Int, low: Int, high: Int): Int = when {
        amount < low -> low
        amount > high -> high
        else -> amount
    }
}

@ColorInt
internal fun Int.adjustAlpha(@FloatRange(from = 0.0, to = 1.0) factor: Float): Int {
    val alpha = (Color.alpha(this) * factor).roundToInt()
    val red = Color.red(this)
    val green = Color.green(this)
    val blue = Color.blue(this)
    return Color.argb(alpha, red, green, blue)
}