package com.caldeirasoft.basicapp.presentation.utils.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat

inline fun Context.toast(message: Int): Toast = Toast
        .makeText(this, message, Toast.LENGTH_SHORT)
        .apply {
            show()
        }

inline fun Context.toast(message: CharSequence): Toast = Toast
        .makeText(this, message, Toast.LENGTH_SHORT)
        .apply {
            show()
        }

inline fun Context.scrimBackground(): Int {
    return themeAttributeToColor(com.google.android.material.R.attr.scrimBackground)
}

@ColorInt
internal fun Context.getThemeColor(@AttrRes attributeName: Int): Int {
    val typedValue = TypedValue()
    val typedArray = obtainStyledAttributes(typedValue.data, intArrayOf(attributeName))
    try {
        return typedArray.getColor(0, Color.BLACK)
    } finally {
        typedArray.recycle()
    }
}

internal val Context.colorOnSurface: Int
    @ColorInt get() = getThemeColor(com.google.android.material.R.attr.colorOnSurface)

internal val Context.colorPrimary: Int
    @ColorInt get() = getThemeColor(com.google.android.material.R.attr.colorPrimary)

internal fun Context.float(@DimenRes dimenRes: Int): Float =
        ResourcesCompat.getFloat(resources, dimenRes)

@ColorInt
internal fun Context.color(@ColorRes color: Int): Int {
    return ContextCompat.getColor(this, color)
}

internal fun Context.colorStateList(@ColorRes color: Int): ColorStateList? {
    return AppCompatResources.getColorStateList(this, color)
}

internal fun Context.drawable(@DrawableRes drawable: Int): Drawable? {
    return AppCompatResources.getDrawable(this, drawable)
}

internal fun Context.font(@FontRes fontId: Int) =
        ResourcesCompat.getFont(this, fontId) ?: throw(Throwable("Font doesn't exist"))

internal fun Context.animatedDrawable(@DrawableRes drawableId: Int) =
        AnimatedVectorDrawableCompat.create(this, drawableId)

fun Context.themeAttributeToColor(themeAttributeId: Int, fallbackColor: Int = Color.WHITE): Int {
    val outValue = TypedValue()
    val theme = this.theme
    val resolved = theme.resolveAttribute(themeAttributeId, outValue, true)
    if (resolved) {
        return ContextCompat.getColor(this, outValue.resourceId)
    }
    return fallbackColor
}

fun Context.themeAttributeToResId(themeAttributeId: Int): Int {
    val outValue = TypedValue()
    val theme = this.theme
    val resolved = theme.resolveAttribute(themeAttributeId, outValue, true)
    if (resolved) {
        return outValue.resourceId
    }
    return -1
}