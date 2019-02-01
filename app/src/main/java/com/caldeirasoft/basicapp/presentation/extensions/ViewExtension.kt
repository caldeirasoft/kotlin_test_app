package com.caldeirasoft.basicapp.presentation.extensions

import android.content.Context
import android.util.Log
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat


fun Context.color(@ColorRes colorId: Int) = ContextCompat.getColor(this, colorId)

fun Context.colorStateList(@ColorRes colorId: Int) = ContextCompat.getColorStateList(this, colorId)

fun Context.drawable(@DrawableRes drawableId: Int) =
        AppCompatResources.getDrawable(this, drawableId)

fun Context.font(@FontRes fontId: Int) =
        ResourcesCompat.getFont(this, fontId) ?: throw(Throwable("Font doesn't exist"))

fun Context.animatedDrawable(@DrawableRes drawableId: Int) =
        AnimatedVectorDrawableCompat.create(this, drawableId)