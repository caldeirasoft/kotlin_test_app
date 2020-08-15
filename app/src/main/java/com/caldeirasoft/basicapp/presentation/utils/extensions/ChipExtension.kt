package com.caldeirasoft.basicapp.presentation.utils.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.presentation.utils.adjustAlpha
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

/**
 * Add a chip to chipGroup
 */
internal inline fun ChipGroup.addChip(crossinline chipCreator: (Context) -> Chip) {
    chipCreator(this.context).let {
        this.addView(it)
    }
}

fun Chip.applyCustomColorToChoiceChip() {
    chipBackgroundColor = this.mtrl_choice_chip_background_color()
    chipStrokeColor = this.mtrl_choice_chip_stroke_color()
    rippleColor = this.mtrl_choice_chip_ripple_color()
    setTextColor(this.mtrl_choice_chip_text_color())
}

// R.color.mtrl_choice_chip_text_color
internal fun View.mtrl_choice_chip_text_color(): ColorStateList {
    val colorPrimary = context.colorPrimary
    val colorOnSurface = context.colorOnSurface
    return ColorStateList(
            arrayOf(
                    intArrayOf(android.R.attr.state_enabled, android.R.attr.state_selected),
                    intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_enabled),
                    intArrayOf()
            ),
            intArrayOf(
                    colorPrimary,
                    colorPrimary,
                    colorOnSurface.adjustAlpha(0.87f),
                    colorOnSurface.adjustAlpha(0.33f)
            )
    )
}

// R.color.mtrl_choice_chip_background_color
private fun View.mtrl_choice_chip_background_color(): ColorStateList {
    val colorPrimaryAlpha24 = context.colorPrimary.adjustAlpha(0.24f)
    val colorOnSurface = context.colorOnSurface
    return ColorStateList(
            arrayOf(
                    intArrayOf(android.R.attr.state_enabled, android.R.attr.state_selected),
                    intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_enabled),
                    intArrayOf()
            ),
            intArrayOf(
                    colorPrimaryAlpha24,
                    colorPrimaryAlpha24,
                    colorOnSurface.adjustAlpha(0f),
                    colorOnSurface.adjustAlpha(0f)
            )
    )
}

// R.color.mtrl_choice_chip_stroke_color
private fun View.mtrl_choice_chip_stroke_color(): ColorStateList {
    val colorPrimaryAlpha24 = context.colorPrimary.adjustAlpha(0.24f)
    val colorOnSurface = context.colorOnSurface
    return ColorStateList(
            arrayOf(
                    intArrayOf(android.R.attr.state_enabled, android.R.attr.state_selected),
                    intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_enabled),
                    intArrayOf()
            ),
            intArrayOf(
                    colorPrimaryAlpha24,
                    colorPrimaryAlpha24,
                    colorOnSurface.adjustAlpha(0.10f),
                    colorOnSurface.adjustAlpha(0.12f)
            )
    )
}

// R.color.mtrl_choice_chip_ripple_color
private fun View.mtrl_choice_chip_ripple_color(): ColorStateList {
    val colorPrimary = context.colorPrimary
    val colorOnSurface = context.colorOnSurface
    return ColorStateList(
            arrayOf(
                    intArrayOf(android.R.attr.state_pressed),
                    intArrayOf(android.R.attr.state_focused, android.R.attr.state_hovered),
                    intArrayOf(android.R.attr.state_focused),
                    intArrayOf(android.R.attr.state_hovered),
                    intArrayOf()
            ),
            intArrayOf(
                    colorPrimary.adjustAlpha(context.float(R.dimen.mtrl_low_ripple_pressed_alpha)),
                    colorOnSurface.adjustAlpha(context.float(R.dimen.mtrl_low_ripple_focused_alpha)),
                    colorOnSurface.adjustAlpha(context.float(R.dimen.mtrl_low_ripple_focused_alpha)),
                    colorOnSurface.adjustAlpha(context.float(R.dimen.mtrl_low_ripple_hovered_alpha)),
                    colorOnSurface.adjustAlpha(context.float(R.dimen.mtrl_low_ripple_default_alpha))
            )
    )
}