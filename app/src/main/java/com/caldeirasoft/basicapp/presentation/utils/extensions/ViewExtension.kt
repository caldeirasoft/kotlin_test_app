package com.caldeirasoft.basicapp.presentation.utils.extensions

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat


fun Context.color(@ColorRes colorId: Int) = ContextCompat.getColor(this, colorId)

fun Context.colorStateList(@ColorRes colorId: Int) = ContextCompat.getColorStateList(this, colorId)

fun Context.drawable(@DrawableRes drawableId: Int) =
        AppCompatResources.getDrawable(this, drawableId)

fun Context.font(@FontRes fontId: Int) =
        ResourcesCompat.getFont(this, fontId) ?: throw(Throwable("Font doesn't exist"))

fun Context.animatedDrawable(@DrawableRes drawableId: Int) =
        AnimatedVectorDrawableCompat.create(this, drawableId)


@ColorInt
fun Context.getThemeColor(@AttrRes attributeName: Int): Int {
    val typedValue = TypedValue()
    val typedArray = obtainStyledAttributes(typedValue.data, intArrayOf(attributeName))
    try {
        return typedArray.getColor(0, Color.BLACK)
    } finally {
        typedArray.recycle()
    }
}