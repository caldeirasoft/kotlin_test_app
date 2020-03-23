package com.caldeirasoft.basicapp.presentation.utils

import android.graphics.Color

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